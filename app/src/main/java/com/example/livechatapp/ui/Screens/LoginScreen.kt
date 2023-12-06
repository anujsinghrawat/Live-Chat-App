package com.example.livechatapp.ui.Screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.livechatapp.CheckSignedIn
import com.example.livechatapp.CommonProgressIndicator
import com.example.livechatapp.DestinationScreen
import com.example.livechatapp.LCViewModel
import com.example.livechatapp.R
import com.example.livechatapp.navigateTo
import com.example.livechatapp.ui.components.MyButton
import com.example.livechatapp.ui.components.MyTextField

@Composable
fun LoginScreen(navController: NavController,vm : LCViewModel)
    {


        CheckSignedIn(vm = vm ,navController = navController)
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentHeight()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                var emailState = remember{
                    mutableStateOf<String>("")
                }
                var passwordState = remember{
                    mutableStateOf<String>("")
                }
                val focus = LocalFocusManager.current
                Image(
                    painter = painterResource(id = R.drawable.logo), contentDescription = "chat logo",
                    modifier = Modifier
                        .width(200.dp)
                        .padding(top = 16.dp)
                        .padding(8.dp)
                )
                Text(text = "SignUp",
                    fontSize = 30.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold
                    , modifier = Modifier.padding(8.dp)
                )
                MyTextField(
                    value = emailState.value,
                    onValueChanged = {
                        emailState.value = it
                    },
                    label = "Email",
                    modifier = Modifier.padding(8.dp))
                MyTextField(
                    value = passwordState.value,
                    onValueChanged = {
                        passwordState.value = it
                    },
                    isPassword = true,
                    label = "Password",
                    modifier = Modifier.padding(8.dp))
                MyButton(
                    label = "LogIn",
                    onClick = {
                        Log.d("TAG", "LoginScreen: email is ${emailState.value} and password is ${passwordState.value}")
                        vm.login(
                            emailState.value,
                            passwordState.value
                        )

                    },
                )
                Text(text = "New User ? Go to SignUp -> ",
                    color = Color.Blue,
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable {
                            navigateTo(navController, DestinationScreen.SignUp.route)
                        })
            }
        }
        if(vm.isLoading.value){
            CommonProgressIndicator()
        }
    }
