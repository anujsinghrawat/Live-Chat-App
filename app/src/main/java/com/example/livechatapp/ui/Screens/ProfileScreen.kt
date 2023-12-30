package com.example.livechatapp.ui.Screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.livechatapp.CommonDivider
import com.example.livechatapp.CommonImage
import com.example.livechatapp.CommonProgressIndicator
import com.example.livechatapp.DestinationScreen
import com.example.livechatapp.LCViewModel
import com.example.livechatapp.navigateTo
import com.example.livechatapp.ui.components.BottomNavigationItem
import com.example.livechatapp.ui.components.BottomNavigationMenu
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment

@Composable
fun ProfileScreen(
    navController: NavController,
    vm: LCViewModel
) {
    val inProgess = vm.isLoading.value

    if(inProgess){
        CommonProgressIndicator()
    }else{
        val userData = vm.userData.value
        var name by rememberSaveable {
            mutableStateOf(
                userData?.name?:""
            )
        }
        var number by rememberSaveable {
            mutableStateOf(
                userData?.number?:""
            )
        }
        Scaffold(
            bottomBar = {
                BottomNavigationMenu(
                    selectedItem = BottomNavigationItem.PROFILE,
                    navController= navController
                )
            }
        ) {
            paddingValues ->
            Column() {
                ProfileContent(
                    modifier = Modifier
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState()),
                    vm = vm,
                    name = name,
                    number = number,
                    onNameChanged = {name = it},
                    onNumberChanged = {number = it},
                    onBack = {
                             navigateTo(navController = navController, route = DestinationScreen.ChatList.route)
                             },
                    onSave = {
                             vm.createOrUpdateProfile(
                                 name = name,
                                 number = number
                             )
                             },
                    onLogout = {
                        vm.logout()
                        navigateTo(navController,DestinationScreen.Login.route)
                    })

            }
        }


    }

}

@Composable
fun ProfileContent(
    modifier :Modifier,
    vm: LCViewModel,
    name :String,
    number:String,
    onNameChanged: (String) ->Unit,
    onNumberChanged: (String) ->Unit,
    onBack: () ->Unit,
    onSave: () ->Unit,
    onLogout: () ->Unit,
) {
    val imageUrl = vm.userData.value?.imageUrl
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    )
         {
            Row(modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween

            ) {
                Text("Back",Modifier.clickable {
                    onBack.invoke()
                }, color = Color.White)

                Text("Save",Modifier.clickable {
                    onSave.invoke()
                })
            }

            CommonDivider()
            ProfileImage(
                imageUrl = imageUrl,
                vm = vm
            )
            CommonDivider()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Name : ")
                TextField(value = name, onValueChange = onNameChanged,
                    colors = TextFieldDefaults.colors(
//                        focusedTextColor = Color.Black,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent
                    )
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Number :")
                TextField(value = number, onValueChange = onNumberChanged,
                    colors = TextFieldDefaults.colors(
//                        focusedTextColor = Color.Black,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent
                    )
                )
            }
            CommonDivider()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center
            ){
                Text(text = "Logout", modifier = Modifier.clickable {
                    onLogout.invoke()
                })
            }

        }
    }

@Composable
fun ProfileImage(
    imageUrl:String?,
    vm :LCViewModel
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()  ){
        uri ->
        uri?.let {
            vm.updateProfileImage(uri)
        }
    }
    Box(modifier = Modifier.height(intrinsicSize = IntrinsicSize.Min)){
        Column (
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .clickable {
                    launcher.launch("image/*")
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Card(
                shape = CircleShape,
                modifier = Modifier
                    .padding(8.dp)
                    .size(100.dp)
            ) {
                CommonImage(data = imageUrl)
            }
            Text(text = "Change Profile Picture")

        }
        if(vm.isLoading.value)
        {
            CommonProgressIndicator()
        }
    }
}