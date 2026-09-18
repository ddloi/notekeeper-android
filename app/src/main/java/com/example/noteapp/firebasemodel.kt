package com.example.noteapp

//class firebasemodel {
//    var title: String? = null
//    var content: String? = null
//
//    constructor()
//
//    constructor(title: String?, content: String?) {
//        this.title = title
//        this.content = content
//    }
//}
class firebasemodel {
    var title: String? = null
    var content: String? = null
    var date: String? = null       // Ngày tạo/cập nhật
    var color: String? = null      // Màu nền note (hex string, vd: "#FFD700")
    var priority: Int = 0          // 0 = Thấp, 1 = Trung bình, 2 = Cao

    constructor()

    constructor(title: String?, content: String?, date: String?, color: String?, priority: Int) {
        this.title = title
        this.content = content
        this.date = date
        this.color = color
        this.priority = priority
    }
}


