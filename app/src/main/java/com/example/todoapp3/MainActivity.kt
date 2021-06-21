package com.example.todoapp3

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    lateinit var realm: Realm
    lateinit var result: RealmResults<TaskDB>  //データの塊(コレクション)
    lateinit var task_list: ArrayList<String>
    lateinit var  adapter: ArrayAdapter<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //新しいタスクの追加ボタンを押すと画面遷移
        newTaskButton.setOnClickListener {
            val intent = Intent(this@MainActivity, MainActivity2::class.java)
            //タスクの追加画面とタスクの修正画面はテキストのタイトルが違うだけなので、使いまわす
            //その時に、次の画面に行った時にタスクの追加　か　タスクの修正の文字を出すため
            //「タスクの追加」ボタンを押したときは、タスクの追加の文字を渡している
            intent.putExtra(getString(R.string.intent_key_status), getString(R.string.new_task))
            startActivity(intent)
        }

        listView.setOnItemClickListener(this)
        listView.setOnItemLongClickListener(this)
    }

        //ListViewにすでにあるタスクをタップした時
        override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

            val selectDB = result[position]
            val strSelectTime = result[position]?.strTime
            val strSelectTask = result[position]?.strTask
            val strSelectFrag = result[position]?.finishFrag
            val intent = Intent(this@MainActivity, MainActivity2::class.java)
            //(行番号)番目のタスクの時間
            intent.putExtra(getString(R.string.intent_key_time), strSelectTime)
            //(行番号)番目のタスクの内容
            intent.putExtra(getString(R.string.intent_key_task), strSelectTask)
            //(行番号)番目のタスクが完了(チェックマーク)しているかどうか
            //二択になるものはフラグ
            intent.putExtra(getString(R.string.intent_key_frag), strSelectFrag)
            //タスクの修正という文字列
            intent.putExtra(getString(R.string.intent_key_position), position)
            //行番号
            intent.putExtra(
                //タスクの追加or修正のステータスキーから、タスクの修正という情報を渡す
                getString(R.string.intent_key_status), getString(R.string.status_change))
            startActivity(intent)
        }


    //ListViewの表示方法
    override fun onResume() {
        super.onResume()
        realm = Realm.getDefaultInstance()  //データベースの使用開始

        //データベースからデータを取得
        //lateinit var result: RealmResults
        result = realm.where(TaskDB::class.java).findAll().sort("strTime")

        //データの塊(コレクション)の種類
        task_list = ArrayList()

        //取得したデータの行数
        //1行ずつ取り出すために宣言する
        val length = result.size

        for (i in 0 .. length-1) {  //0番目からlength-1番目までループ
            if (result[i]!!.finishFrag) {  //もしresult[i].finishFragの値がtrueなら
                //result[i]!!.strTime + ":" + result[i]!!.strTask + "✔️"をコレクションに追加
                //    変数 + “文字列”と書くと、変数文字列　と出力される
                task_list.add(result[i]!!.strTime + ":" + result[i]!!.strTask + "✔️")
            } else {
                //そうでなければ、result[i]!!.strTime + ":" + result[i]!!.strTaskをコレクションに追加
                task_list.add(result[i]!!.strTime + ":" + result[i]!!.strTask)
            }
        }

        //コレクションをListViewに反映させるにあたってアダプターというものを使用して表示する
        adapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,task_list)
        listView.adapter = adapter

    }


    override fun onItemLongClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long):Boolean
    {
        //選択した行番号が格納されている
        val selectDB = result[position]!!
        //AlertDialog.Builderのインスタンス化
        val dialog = AlertDialog.Builder(this@MainActivity).apply {
            setTitle(selectDB.strTask + "の削除")
            setMessage("削除しても良いですか？")
            setPositiveButton("yes") {
                //Yesボタン押した時の処理
                dialog, which ->

                //長押しした行のデータの削除
                realm.beginTransaction()
                selectDB.deleteFromRealm()
                realm.commitTransaction()

                //長押しした行のリストの削除
                task_list.removeAt(position)
                //画面更新、アダプターの再接続
                listView.adapter = adapter
            }
            setNegativeButton("no") {
                dialog, which ->

            }
            show()
        }

        return true
    }

    override fun onPause() {
        super.onPause()
        realm.close()
    }


}