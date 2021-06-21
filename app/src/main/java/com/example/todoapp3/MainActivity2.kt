package com.example.todoapp3

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main2.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity2 : AppCompatActivity() {

    //Realmのインスタンス化するための変数定義
    lateinit var realm: Realm
    //データを保存するための変数定義
    var strTime: String = ""
    var strTask: String = ""
    var intposition: Int = 0
    var boolMemorize: Boolean = false

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        val df = SimpleDateFormat("MM/dd HH:mm")
        val date = Date()
        //editTextDate.setText(df.format(date))
        val bundle = intent.extras

        val strStatus = bundle?.getString(getString(R.string.intent_key_status))
        //二枚目の画面で、タイトルが「タスクの追加」か「修正画面」かになるようにしている
        //どっちになるかは、一枚目の画面からintentで送っている
        textViewStatus.text = strStatus

        if (strStatus == getString(R.string.new_task)) {
            editTextTask.setText("")
            editTextDate.setText(df.format(date))
        }

        if (strStatus == getString(R.string.status_change)) {
            strTime = bundle.getString(getString(R.string.intent_key_time))!!
            strTask = bundle.getString(getString(R.string.intent_key_task))!!
            boolMemorize = bundle.getBoolean(getString(R.string.intent_key_frag))

            editTextDate.setText(strTime)
            editTextTask.setText(strTask)
            checkBox.isChecked = boolMemorize

            intposition = bundle.getInt(getString(R.string.intent_key_position))

        }


        //登録ボタンを押した時
        registerButton.setOnClickListener {
            //textViewStatusが新しいタスクの追加、に一致した場合
            if (strStatus == getString(R.string.new_task)) {//strStatusの値によって実行されるメゾットを変更する
                //addNewTaskメソッド
                addNewTask()
            } else {
                //以外は、changeTaskメソッド
                changeTask()
            }
        }

        checkBox.setOnClickListener {
            boolMemorize = checkBox.isChecked//チェックボックスの状態を変数に代入
        }

        goBackButton.setOnClickListener {

            finish()//元の画面に戻る処理
        }
    }



        //タスクの編集処理
        private fun changeTask() {

            //データベースの全データの取得
            //sortで時間順に表示、データを取得している
            val result = realm.where(TaskDB::class.java).findAll().sort("strTime")
            //全データの中の（前の画面でタップされた行番号）番目のデータを取得
            val selectDB = result[intposition]!!
            realm.beginTransaction()//データベースの使用宣言

            selectDB.strTime = editTextDate.text.toString()//取得した日付の変更
            selectDB.strTask = editTextTask.text.toString()//取得したtaskの変更
            //チェックしたかどうかの記録をする
            selectDB.finishFrag = boolMemorize


            realm.commitTransaction()//データベースの更新

            //何も記入してない状態に戻す
            editTextDate.setText("")
            editTextTask.setText("")

            //トーストで結果を表示
            Toast.makeText(this@MainActivity2, "修正が完了いたしました", Toast.LENGTH_SHORT).show()
            finish()//元の画面に遷移

        }

        //タスクの登録処理
        private fun addNewTask() {
            realm.beginTransaction()//データベースの使用開始

            val taskDB = realm.createObject(TaskDB::class.java)
            taskDB.strTime = editTextDate.text.toString()//strTimeに時間の文字列を追加
            taskDB.strTask = editTextTask.text.toString()//strTaskにタスク内容を文字列で追加
            realm.commitTransaction()//データベースの使用を終了する

            editTextTask.setText("")
            //トーストを出現
            Toast.makeText(this@MainActivity2, "登録が完了しました", Toast.LENGTH_SHORT).show()

        }



        override fun onResume() {
            super.onResume()
            //インスタンスを取得してRealmの使用を可能にする
            realm = Realm.getDefaultInstance() //データベースに新たにデータを追加することを宣言

        }

        override fun onPause() {
            super.onPause()
            //Realmの使用をやめる
            realm.close()
        }

}
