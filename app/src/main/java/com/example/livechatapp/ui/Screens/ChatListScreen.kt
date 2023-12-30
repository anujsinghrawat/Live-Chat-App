package com.example.livechatapp.ui.Screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.livechatapp.CommonProgressIndicator
import com.example.livechatapp.CommonRow
import com.example.livechatapp.DestinationScreen
import com.example.livechatapp.LCViewModel
import com.example.livechatapp.TitleText
import com.example.livechatapp.navigateTo
import com.example.livechatapp.ui.components.BottomNavigationItem
import com.example.livechatapp.ui.components.BottomNavigationMenu
import okhttp3.internal.wait

@Composable
fun ChatListScreen(navController: NavController, vm: LCViewModel) {
    val isLoading = vm.isLoadingChat.value
    if (isLoading) {
        CommonProgressIndicator()
    } else {
        val chats = vm.chats.value
        val userData = vm.userData.value
        val showDialog = remember {
            mutableStateOf(false)
        }
        val onFabClick: () -> Unit = {
            showDialog.value = true
        }
        val onDismiss: () -> Unit = {
            showDialog.value = false
        }
        val onAddChat: (String) -> Unit = {
            vm.onAddChat(it)
            showDialog.value = false
        }
        Scaffold(
            topBar = {
                TitleText(text = "Chats")
            },
            floatingActionButton = {
                FAB(
                    showDialog = showDialog.value,
                    onFabClick = onFabClick,
                    onDismiss = onDismiss,
                    onAddChat = onAddChat
                )
            },
            bottomBar = {
                BottomNavigationMenu(
                    selectedItem = BottomNavigationItem.CHATLIST,
                    navController = navController
                )
            },
            content = { paddingValues ->
                if (chats.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                        content = {
                            Text(text = "No Chats Available")
                        }
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        items(chats){
                            chat->
                            val chatUser = if(chat.user1.userId == userData?.userId){
                                 chat.user2
                            }else{
                                chat.user1
                            }
                            CommonRow(imageUrl = chatUser.imageUrl,name = chatUser.name) {
                                chat.chatId?.let{
                                    navigateTo(navController,DestinationScreen.SingleChat.createRoute(id =it))
                                }
                            }
                        }

                    }


                }
            })


    }


}

@Composable
fun FAB(
    showDialog: Boolean,
    onFabClick: () -> Unit,
    onDismiss: () -> Unit,
    onAddChat: (String) -> Unit,
) {
    val addChatNumber = remember {
        mutableStateOf("")
    }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                onDismiss.invoke()
                addChatNumber.value = ""
            },
            confirmButton = {
                Button(
                    onClick = {
                        onAddChat(addChatNumber.value)
                    }
                ) {
                    Text(text = "Add Chat")
                }
            },
            title = {
                Text(text = "Add Chat")
            },
            text = {
                OutlinedTextField(
                    value = addChatNumber.value,
                    onValueChange = {
                        addChatNumber.value = it
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )
            })
    }
    FloatingActionButton(
        onClick = onFabClick ,
        containerColor = MaterialTheme.colorScheme.secondary,
        shape = CircleShape,
        modifier = Modifier.padding(bottom = 40.dp)
    ) {
        Icon(
            imageVector = Icons.Rounded.Add, contentDescription = null,
            tint = Color.White
        )
    }
}