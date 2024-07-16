package com.huangyuanlove.wehelper.utils

const val Step_idle = -1
const val Step_Start = 1
const val Step_search_contact = 100
const val Step_search_and_click_video = Step_search_contact + 1
const val Step_start_video_chat = Step_search_and_click_video + 1

object   WeChatCallStepManager{


    var action:Action = Action.none
    var step:Int = Step_idle
    var name:String= ""

    fun reset(){
        action = Action.none
        step = Step_idle
        name = ""
    }

}
enum class Action{
    video_call,
    voice_call,
    none
}

