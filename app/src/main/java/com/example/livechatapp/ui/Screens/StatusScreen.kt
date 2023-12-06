package com.example.livechatapp.ui.Screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.livechatapp.LCViewModel
import com.example.livechatapp.ui.components.BottomNavigationItem
import com.example.livechatapp.ui.components.BottomNavigationMenu

@Composable
fun StatusScreen(
    navController: NavController,
    vm:LCViewModel
) {
    BottomNavigationMenu(
        selectedItem = BottomNavigationItem.CHATLIST,
        navController= navController
    )
}