package com.example.livechatapp

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import com.example.livechatapp.data.CHATS
import com.example.livechatapp.data.ChatData
import com.example.livechatapp.data.ChatUser
import com.example.livechatapp.data.Event
import com.example.livechatapp.data.MESSAGE
import com.example.livechatapp.data.Message
import com.example.livechatapp.data.STATUS
import com.example.livechatapp.data.Status
import com.example.livechatapp.data.USER_NODE
import com.example.livechatapp.data.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import java.lang.Exception
import java.util.Calendar
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class LCViewModel @Inject constructor(
    val auth: FirebaseAuth,
    var db: FirebaseFirestore,
    val storage: FirebaseStorage
) : ViewModel() {

    val chats = mutableStateOf<List<ChatData>>(listOf())
    var isLoading = mutableStateOf(false)
    val eventMutableState = mutableStateOf<Event<String>?>(null)
    var isSignedIn = mutableStateOf(false)
    var userData = mutableStateOf<UserData?>(null)

    var isLoadingChat = mutableStateOf(false)
    val chatMessages = mutableStateOf<List<Message>>(listOf())
    val isChatLoading = mutableStateOf(false)
    var currentChatMessageListener :ListenerRegistration?=null

    val status = mutableStateOf<List<Status>>(listOf())
    val isLoadingStatus = mutableStateOf<Boolean>(false)

    init {
        val currentUser = auth.currentUser
        isSignedIn.value = currentUser != null
        currentUser?.uid?.let {
            getUserData(it)
        }
    }

    fun signOut() {
        isLoading.value = true
        auth.signOut()
        isSignedIn.value = false
        userData.value = null
    }

    fun login(email: String, password: String) {
        if (email.isEmpty() or password.isEmpty()) {
            handleException(customMessage = "Please Fill all the Fields")
            return
        } else {
            isLoading.value = true
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        isSignedIn.value = true
                        isLoading.value = false
                        auth.currentUser?.uid?.let {
                            getUserData(it)
                        }
                    } else {
                        handleException(it.exception, "Login Failed")
                    }
                }

        }

    }


    fun signUp(name: String, number: String, email: String, password: String) {
        isLoading.value = true
        if (name.isEmpty() or number.isEmpty() or email.isEmpty() or password.isEmpty()) {
            handleException(customMessage = "Please Fill all the fields")
//            isLoading.value = false
            return
        }
        isLoading.value = true
        db.collection(USER_NODE).whereEqualTo("number", number).get().addOnSuccessListener {
            if (it.isEmpty) {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        isSignedIn.value = true
                        createOrUpdateProfile(name, number)
                        Log.d(
                            "TAG",
                            "signUp:the user has signed up with name is $name ,email is $email  "
                        )
                        isLoading.value = false
                    } else {
                        Log.d("TAG", "signUp: Kya backchodi hai bhaii")
                        handleException(it.exception, customMessage = "SignUp Failed")
                    }
                }
            } else {
                handleException(customMessage = "Number Already Exists")
                isLoading.value = false
            }
        }

    }

    fun createOrUpdateProfile(
        name: String? = null,
        number: String? = null,
        imageUrl: String? = null
    ) {
        var uid = auth.currentUser?.uid
        val userData = UserData(
            userId = uid,
            name = name ?: userData.value?.name,
            number = number ?: userData.value?.number,
            imageUrl = imageUrl ?: userData.value?.imageUrl
        )

        uid?.let {
            isLoading.value = true
            db.collection(USER_NODE).document(
                uid
            ).get().addOnSuccessListener {
                if (it.exists()) {
                    db.collection(USER_NODE).document(uid).set(userData)
                    isLoading.value = false
                    getUserData(uid)
                } else {
                    db.collection(USER_NODE).document(uid).set(userData)
                    isLoading.value = false
                    getUserData(uid)
                }
            }.addOnFailureListener {
                handleException(it, "Cannot Retrieve User")
            }
        }
    }

    private fun getUserData(uid: String) {
        isLoading.value = true
        db.collection(USER_NODE).document(uid).addSnapshotListener { value, error ->
            if (error != null) {
                handleException(error, "Cannot retrieve UserData")
            }
            if (value != null) {
                var user = value.toObject<UserData>()
                userData.value = user
                populateChats()
                populateStatus()
                isLoading.value = false
            }
        }
    }


    fun handleException(exception: Exception? = null, customMessage: String = " ") {
        Log.e("TAG", "handleException: ", exception)
        exception?.printStackTrace()
        val errorMsg = exception?.localizedMessage ?: ""
        val message = if (customMessage.isNullOrEmpty()) errorMsg else customMessage

        eventMutableState.value = Event(message)
        isLoading.value = false
    }

    fun updateProfileImage(uri: Uri) {
        uploadImage(uri) {
            createOrUpdateProfile(imageUrl = it.toString())
        }

    }

    fun populateMessages(chatId:String){
        isChatLoading.value = true
    currentChatMessageListener = db.collection(CHATS).document(chatId).collection(MESSAGE).addSnapshotListener{
        value, error ->
        if(error!=null)
        {
            handleException(error)
            isChatLoading.value = false
        }
        if(value!=null){
            chatMessages.value = value.documents.mapNotNull {
                it.toObject<Message>()
            }.sortedBy { it.timestamp }
            isChatLoading.value = false
        }
    }
    }
    fun depopulateMessage(){
        chatMessages.value = listOf()
        currentChatMessageListener = null
    }

    fun populateChats(){
        isLoadingChat.value = true
    db.collection(CHATS).where(
        Filter.or(
            Filter.equalTo("user1.userId",userData.value?.userId),
            Filter.equalTo("user2.userId",userData.value?.userId)
        )
    ).addSnapshotListener{
        value,error->
        if(error!=null){
            handleException(error)

        }
        if(value!=null){
        chats.value = value.documents.mapNotNull {
            it.toObject<ChatData>()
        }
        isLoadingChat.value = false
    }
    }
    }

    fun uploadImage(uri: Uri, onSuccess: (Uri) -> Unit) {
        isLoading.value = true
        val storageRef = storage.reference
        val uuid = UUID.randomUUID()
        val imageRef = storageRef.child("images/$uuid")
        imageRef.putFile(uri)
            .addOnSuccessListener {

                val result = it.metadata?.reference?.downloadUrl
                result?.addOnSuccessListener(onSuccess)
                print("$result")
                isLoading.value = false
            }.addOnFailureListener {
                Log.d("TAG", "uploadImage: failed")
                handleException(it)
                isLoading.value = false
            }
    }

    fun onSendReply(chatId:String,message:String){
        val time = Calendar.getInstance().time.toString()
        val msg = Message(
            userData.value?.userId,message,time
        )
db.collection(CHATS).document(chatId).collection(MESSAGE).document().set(msg)
    }

    fun logout() {
        isLoading.value = true
        depopulateMessage()
        currentChatMessageListener = null
        auth.signOut()
        signOut()
        userData.value = null
        eventMutableState.value = Event("Logged out")
        isLoading.value = false
//        navigateTo(navController = )
    }

    fun onAddChat(number: String) {
        if (number.isEmpty() or !number.isDigitsOnly()) {
            handleException(customMessage = "Number must be of 10 digits!")
        } else {
            db.collection(CHATS).where(
                Filter.or(
                    Filter.and(
                        Filter.equalTo("user1.number", number),
                        Filter.equalTo("user2.number", userData.value?.number)
                    ),
                    Filter.and(
                        Filter.equalTo("user1.number", userData.value?.number),
                        Filter.equalTo("user2.number", number),

                        )
                )
            ).get().addOnSuccessListener {
                if(it.isEmpty){
                db.collection(USER_NODE).whereEqualTo("number",number).get().addOnSuccessListener {
                    if(it.isEmpty){
                        handleException(customMessage = "Number  not Found")
                    }else{
                        val chatPartner= it.toObjects<UserData>()[0]
                        val id = db.collection(CHATS).document().id
                        val chat = ChatData(
                            chatId = id,
                            ChatUser(
                                userData.value?.userId,
                                userData.value?.name,
                                userData.value?.imageUrl,
                                userData.value?.number,
                            ),
                            ChatUser(
                                chatPartner.userId,
                                chatPartner.name,
                                chatPartner.imageUrl,
                                chatPartner.number,
                            )
                        )
                        db.collection(CHATS).document().set(chat)
                    }
                }.addOnFailureListener {
                    handleException(it)
                }
                }else{
                    handleException(customMessage = "Chats Already Exists")
                }
            }
        }
    }

    fun uploadStatus(uri: Uri) {
        uploadImage(uri){
            createStatus(it.toString())
        }
    }

    fun populateStatus(){
        val timeDelta = 24L*60*60*1000
        val cutOff = System.currentTimeMillis() - timeDelta
        isLoadingStatus.value = true
        db.collection(CHATS).where(
            Filter.or(
                Filter.equalTo("user1.userId",userData.value?.userId),
                Filter.equalTo("user2.userId",userData.value?.userId)

            )
        ).addSnapshotListener{
            value, error ->
            if(error != null)
            {
                handleException(error)
            }
            if(value!= null){
                val currentConnections = arrayListOf(userData.value?.userId)
                val chats = value.toObjects<ChatData>()
                chats.forEach{
                    chat->
                    if(chat.user1.userId == userData.value?.userId){
                        currentConnections.add(chat.user2.userId)
                    }else{
                        currentConnections.add(chat.user1.userId)
                    }
                    db.collection(STATUS).whereGreaterThan("timestamp",cutOff).whereIn("user.userId",currentConnections)
                        .addSnapshotListener{
                            value,error ->
                            if(error!= null)
                            {
                                handleException(error)
                            }
                            if(value!=null)
                            {
                                status.value = value.toObjects()
                                isLoadingStatus.value = false
                            }
                        }
                }
            }

        }
    }

    private fun createStatus(imageUrl:String){
        val newStatus = Status(
            ChatUser(
                userData.value?.userId,
                userData.value?.name ,
                userData.value?.imageUrl,
                userData.value?.number,
            ),
            imageUrl,
            System.currentTimeMillis()
        )
    db.collection(STATUS).document().set(newStatus)
    }
}