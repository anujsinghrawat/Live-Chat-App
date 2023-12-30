package com.example.livechatapp.ui.Screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.livechatapp.CommonDivider
import com.example.livechatapp.CommonImage
import com.example.livechatapp.LCViewModel
import com.example.livechatapp.data.Message

@Composable
fun SingleChatScreen(navController: NavController, vm: LCViewModel, chatId: String) {
    var reply by rememberSaveable {
        mutableStateOf<String>("")
    }
    val onSendReply = {
        vm.onSendReply(chatId, reply)
        reply = ""
    }
    val myUser = vm.userData.value
    val currentChat = vm.chats.value.first{it.chatId == chatId}
    val chatUser = if(myUser?.userId == currentChat.user1.userId) currentChat.user2 else currentChat.user1
    var chatMessages = vm.chatMessages
    LaunchedEffect(key1 = Unit){
        vm.populateMessages(chatId)
    }
    BackHandler {
        vm.depopulateMessage()
    }
    Column(

    ) {
        ChatHeader(name = chatUser.name?:"", imageUrl = chatUser.imageUrl?:"") {
        navController.popBackStack()
            vm.depopulateMessage()
    }
        MessageBox(chatMessages = chatMessages.value  , currentUserId = myUser?.userId?:"")
        ReplyBox(reply = reply, onReplyChange = {
            reply = it
        }, onSendReply = onSendReply)
    }

}

@Composable
fun MessageBox(
    modifier :Modifier =Modifier,
    chatMessages:List<Message>,
    currentUserId:String
) {
    LazyColumn(modifier = modifier){
        items(chatMessages){
            msg->
            val alignment = if(msg.sender == currentUserId) Alignment.End else Alignment.Start
            val color = if(msg.sender == currentUserId) Color.Blue else Color.White
            Column(
                modifier = Modifier.fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = alignment

            ) {
                Text(text = msg.message?:"",
                    modifier= Modifier.clip(RoundedCornerShape(8.dp))
                        .background(color)
                        .padding(12.dp),
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold)
            }

        }
    }
}

@Composable
fun ChatHeader(
    name: String,
    imageUrl: String,
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Rounded.ArrowBack,
            contentDescription = "",
            modifier = Modifier
                .clickable {
                    onBack.invoke()
                }
                .padding(8.dp)
        )
        CommonImage(
            data = imageUrl, modifier = Modifier
                .padding(8.dp)
                .size(50.dp)
                .clip(CircleShape)
        )
        Text(text = name, fontWeight = FontWeight.Bold,modifier = Modifier.padding(start =8.dp))
    }
}

@Composable
fun ReplyBox(
    reply: String,
    onReplyChange: (String) -> Unit,
    onSendReply: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        CommonDivider()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextField(
                value = reply, onValueChange = onReplyChange,
                maxLines = 3,
            )
            Button(onClick = onSendReply) {
                Icon(imageVector = Icons.Rounded.Send, contentDescription = "Send Messages")
            }

        }
    }
}