package com.example.newsapp.util


//Class that is recommended by google to be used to wrap around our network responses, a generic class to differentiate and handle
//the success, failure and loading states.
sealed class Resource<T>(  //a sealed class is similar to abstract class, but we can define which all classes can inherit from it.
        val data:T? = null,
        val message: String? = null) {

    //Only the classes we define here can inherit from this sealed class.

    class Success<T>(data:T):Resource<T>(data)   //inheriting from Resource sealed class
    class Error<T>(message:String,data:T?=null):Resource<T>(data,message)   //inheriting from Resource sealed class

    //When we fire the request this class object will be emitted, when we get the response either the Success or Error
    //object will be emitted.
    class Loading<T>:Resource<T>()   //inheriting from Resource sealed class


}