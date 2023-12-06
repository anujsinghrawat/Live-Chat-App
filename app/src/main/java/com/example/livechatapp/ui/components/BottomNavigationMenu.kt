package com.example.livechatapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AmpStories
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Message
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.livechatapp.DestinationScreen
import com.example.livechatapp.navigateTo


enum class BottomNavigationItem(val icon:ImageVector,val navDestination : DestinationScreen){
    CHATLIST(
        icon = Icons.Default.Message,
        navDestination = DestinationScreen.ChatList
    ),
    STATUSLIST(
        icon = Icons.Default.AmpStories,
        navDestination = DestinationScreen.StatusList
    ),
    PROFILE(
        icon = Icons.Default.Face,
        navDestination = DestinationScreen.Profile
    )
}
@Composable
fun BottomNavigationMenu(
    selectedItem : BottomNavigationItem,
    navController: NavController
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 4.dp)
            .background(Color.White)
    ){
        for(item in BottomNavigationItem.values()){
            Image(imageVector = item.icon,
                contentDescription = item.icon.toString(),
                modifier = Modifier.size(40.dp)
                    .padding(4.dp)
                    .weight(1f)
                    .clickable {
                        navigateTo(navController,item.navDestination.route)
                    },
                colorFilter = if(item == selectedItem)
                                ColorFilter.tint(color = MaterialTheme.colorScheme.inversePrimary) else
                                ColorFilter.tint(color = Color.White)
            )
        }

    }

}