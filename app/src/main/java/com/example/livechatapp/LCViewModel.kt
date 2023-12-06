package com.example.livechatapp

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.livechatapp.data.Event
import com.example.livechatapp.data.USER_NODE
import com.example.livechatapp.data.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import dagger.hilt.android.lifecycle.HiltViewModel
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class LCViewModel @Inject constructor(
    val auth :FirebaseAuth,
    var db :FirebaseFirestore
):ViewModel(){

    var isLoading = mutableStateOf(false)
    val eventMutableState= mutableStateOf<Event<String>?>(null)
    var isSignedIn = mutableStateOf(false)
    var userData = mutableStateOf<UserData?>(null)

    init {
        val currentUser = auth.currentUser
        isSignedIn.value = currentUser!= null
        currentUser?.uid?.let{
            getUserData(it)
        }
    }

    fun signOut(){
        isLoading.value = true
        auth.signOut()
        isSignedIn.value = false
        userData.value = null
    }

    fun login(email: String, password: String){
        if(email.isEmpty() or password.isEmpty()){
            handleException(customMessage = "Please Fill all the Fields")
            return
        }
        else{
            isLoading.value= true
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                if(it.isSuccessful){
                    isSignedIn.value = true
                    isLoading.value = false
                    auth.currentUser?.uid?.let{
                        getUserData(it)
                    }
                }else{
                    handleException(it.exception,"Login Failed")
                }
            }

        }

    }


    fun signUp(name :String ,number:String,email :String,password:String){
        isLoading.value = true
        if(name.isEmpty() or number.isEmpty() or email.isEmpty() or password.isEmpty()){
            handleException(customMessage = "Please Fill all the fields")
//            isLoading.value = false
            return
        }
        isLoading.value = true
        db.collection(USER_NODE).whereEqualTo("number",number).get().addOnSuccessListener {
            if(it.isEmpty){
                auth.createUserWithEmailAndPassword(email , password).addOnCompleteListener {
                    if(it.isSuccessful){
                        isSignedIn.value = true
                        createOrUpdateProfile(name, number)
                        Log.d("TAG", "signUp:the user has signed up with name is $name ,email is $email  ")
                        isLoading.value = false
                    }
                    else{
                        Log.d("TAG", "signUp: Kya backchodi hai bhaii")
                        handleException(it.exception , customMessage = "SignUp Failed")
                    }
                }
            }
            else{
                handleException(customMessage = "Number Already Exists")
                isLoading.value = false
            }
        }

    }

    fun createOrUpdateProfile(name: String?=null, number: String? = null, imageUrl:String? = null) {
        var uid = auth.currentUser?.uid
        val userData = UserData(
            userId = uid,
            name = name ?: userData.value?.name,
            number = number ?: userData.value?.number,
            imageUrl  = imageUrl?:userData.value?.imageUrl
        )

        uid?.let {
            isLoading.value = true
            db.collection(USER_NODE).document(
                uid
            ).get().addOnSuccessListener {
                if(it.exists()){

                }else{
                    db.collection(USER_NODE).document(uid).set(userData)
                    isLoading.value= false
                    getUserData(uid)
                }
            }.addOnFailureListener{
                handleException(it,"Cannot Retrive User")
            }
        }
    }

    private fun getUserData(uid:String) {
        isLoading.value = true
        db.collection(USER_NODE).document(uid).addSnapshotListener{
            value, error ->
            if(error != null){
                handleException(error,"Cannot retreive UserData")
            }
            if(value != null) {
                var user = value.toObject<UserData>()
                userData.value = user
                isLoading.value = false
            }
        }
    }




    fun handleException(exception: Exception? = null,customMessage : String = " "){
        Log.e("TAG", "handleException: ",exception )
        exception?.printStackTrace()
        val errorMsg = exception?.localizedMessage?:""
        val message = if(customMessage.isNullOrEmpty()) errorMsg else customMessage

        eventMutableState.value= Event(message)
        isLoading.value = false
    }

//   TODO : create a composable to show toast message
}