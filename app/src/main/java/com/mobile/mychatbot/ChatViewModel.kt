package com.mobile.mychatbot

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    val messageList by lazy {
        mutableStateListOf<MessageModel>()
    }

    val generativeModel = GenerativeModel(
        // The Gemini 1.5 models are versatile and work with both text-only and multimodal prompts
        modelName = "gemini-1.5-flash",
        // Access your API key as a Build Configuration variable (see "Set up your API key" above)
        apiKey = Constants.apiKey
    )


    @SuppressLint("NewApi")
    fun messageSend(question : String) {
        viewModelScope.launch {
           try {
               val chat = generativeModel.startChat(
                   history=messageList.map {
                       content(it.role){text(it.message)}
                   }.toList()
               )
               messageList.add(MessageModel(question,role = "user"))
              messageList.add(MessageModel("Typing...","model"))

               val responseChat=chat.sendMessage(question)
               messageList.removeLast<MessageModel>()
               messageList.add(MessageModel(responseChat.text.toString(),role ="model"))

           }catch (e :Exception){
               messageList.removeLast<MessageModel>()
               messageList.add(MessageModel("Error : "+e.message.toString(),"model"))
             //  Log.e("error gemini : ",e.printStackTrace().toString())
           }
        }
    }

}