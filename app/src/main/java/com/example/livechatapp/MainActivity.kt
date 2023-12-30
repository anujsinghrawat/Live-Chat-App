package com.example.livechatapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.composable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.hilt.navigation.compose.hiltViewModel

import androidx.navigation.compose.rememberNavController
import com.example.livechatapp.ui.Screens.ChatListScreen
import com.example.livechatapp.ui.Screens.LoginScreen
import com.example.livechatapp.ui.Screens.ProfileScreen
import com.example.livechatapp.ui.Screens.SignUpScreen
import com.example.livechatapp.ui.Screens.SingleChatScreen
import com.example.livechatapp.ui.Screens.SingleStatusScreen
import com.example.livechatapp.ui.Screens.StatusScreen
import com.example.livechatapp.ui.theme.LiveChatAppTheme
import dagger.hilt.android.AndroidEntryPoint


sealed class DestinationScreen(var route: String){
    object SignUp : DestinationScreen("signUp")
    object Login : DestinationScreen("login")
    object Profile : DestinationScreen("profile")

    object ChatList : DestinationScreen("chatList")
    object SingleChat : DestinationScreen("singleChat/{chatId}"){
        fun createRoute(id :String) = "singleChat/$id"
    }
    object StatusList : DestinationScreen("statusList")
    object SingleStatus : DestinationScreen("singleStatus/{userId}"){
        fun createRoute(id :String) = "singleStatus/$id"
    }

}
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LiveChatAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ChatAppNavigation()

                }
            }
        }
    }

    @Composable
    fun ChatAppNavigation() {
        val navController = rememberNavController()
        var vm = hiltViewModel<LCViewModel>()
        NavHost(navController = navController, startDestination =  DestinationScreen.SignUp.route){
            composable(DestinationScreen.SignUp.route){
                SignUpScreen(navController,vm)
            }
            composable(DestinationScreen.Login.route){
                LoginScreen(navController,vm)
            }
            composable(DestinationScreen.Profile.route){
                ProfileScreen(navController,vm)
            }
            composable(DestinationScreen.ChatList.route){
                ChatListScreen(navController,vm)
            }
            composable(DestinationScreen.SingleChat.route){
                val chatId = it.arguments?.getString("chatId")
                chatId?.let{
                    SingleChatScreen(navController,vm,chatId)
                }

            }
            composable(DestinationScreen.StatusList.route){
                StatusScreen(navController,vm)
            }
            composable(DestinationScreen.SingleStatus.route){
                val userId = it.arguments?.getString("userId")
                userId?.let{
                    SingleStatusScreen(navController,vm,it)

                }

            }
        }
//        LoginScreen()
    }
}

