package com.example.progemanag.firebase

import android.util.Log
import com.example.progemanag.activities.SignInActivity
import com.example.progemanag.activities.SignUpActivity
import com.example.progemanag.models.User
import com.example.progemanag.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

/**
 * Firestore 데이터 관리 클래스
 * Firestore는 데이터 엔티티를 개별적으로 관리하지 않고
 * 수직적으로 엔티티에 연관된 엔티티를 하위 클래스처럼 관리하여
 * 필요한 데이터를 추출할 때 복합적으로 인덱스를 사용해서 추출하지 않고
 * 컬렉션에서 서브컬렉션으로 접근하여 데이터를 가져온다.
 * 그러므로 상위 컬렉션에 대한 정보를 가져올 때도 제한 없이 쉽게 가져온다.
 * eg. path = collection1/document1/collection2/documet2
 * https://firebase.google.com/docs/firestore/data-model
 * https://firebase.google.com/docs/firestore/manage-data/structure-data
 *
 */
class FirestoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()

    // Firestore에 컬렉션 등록
    fun registerUser(activity: SignUpActivity, userInfo: User){
        mFireStore.collection(Constants.USER) // 컬렉션 생성
                .document(getCurrentUserID()) // 문서의 식별자를 uuid로 설정
                .set(userInfo, SetOptions.merge()) // 단일 문서를 만들거나 덮어쓰기 위해 set() 사용, 기존 문서가 있다면 merge()
                .addOnSuccessListener {
                    activity.userRegisteredSuccess()
                }.addOnFailureListener { e ->
                    Log.e(activity.javaClass.simpleName,"Error writing documentation", e)
                }
    }

    // Sign In
    fun signInUser(activity: SignInActivity){
        mFireStore.collection(Constants.USER)
                .document(getCurrentUserID())
                .get()
                .addOnSuccessListener { document ->
                    document.toObject(User::class.java)?.also {
                        activity.signInSuccess(it)
                    }
                }.addOnFailureListener { e->
                    Log.d("UID", getCurrentUserID())
                    Log.e("SigInUser", "Error writing document", e)
                }
    }
    private fun getCurrentUserID(): String {
        return FirebaseAuth.getInstance().currentUser!!.uid
    }
}