package com.example.todoapp3

import io.realm.RealmObject

//Realmの型を決める
open class TaskDB : RealmObject() {
    open var strTime: String = ""
    open var strTask: String = ""
    open var finishFrag: Boolean = false

}