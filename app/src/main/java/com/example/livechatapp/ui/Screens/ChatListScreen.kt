package com.example.livechatapp.ui.Screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.livechatapp.DestinationScreen
import com.example.livechatapp.LCViewModel
import com.example.livechatapp.navigateTo
import com.example.livechatapp.ui.components.BottomNavigationItem
import com.example.livechatapp.ui.components.BottomNavigationMenu

@Composable
fun ChatListScreen(navController: NavController, vm: LCViewModel) {
    Column {
        Text(text = "Chat List Screen  and the current user is ${vm.userData.value}")
        Spacer(modifier = Modifier.height(20.dp))
        IconButton(onClick = {
            vm.signOut()
            navigateTo(navController = navController,
                route = DestinationScreen.SignUp.route)
        }) {
            Icon(imageVector = Icons.Default.Logout, contentDescription = "")
        }
        BottomNavigationMenu(
            selectedItem = BottomNavigationItem.CHATLIST,
            navController= navController
        )

    }
}