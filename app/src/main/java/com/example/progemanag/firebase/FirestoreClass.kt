package com.example.progemanag.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.example.progemanag.activities.*
import com.example.progemanag.models.Board
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
 *
 * Firestore 클래스를 따로 만드는 이유
 * 나중에 Firestore를 쓰지 않을 수 있어서 Model을 통해 쉽게 옮길 수 있음.
 * Firestore에 너무 의존해서 작성을 하면 나중에 코드를 싹 갈아 엎어야 함.
 */
class FirestoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()

    // Firestore에 컬렉션 등록
    fun registerUser(activity: SignUpActivity, userInfo: User) {
        mFireStore.collection(Constants.USER) // 컬렉션 생성
                .document(getCurrentUserID()) // 문서의 식별자를 uuid로 설정
                .set(userInfo, SetOptions.merge()) // 단일 문서를 만들거나 덮어쓰기 위해 set() 사용, 기존 문서가 있다면 merge()
                .addOnSuccessListener {
                    activity.userRegisteredSuccess()
                }.addOnFailureListener { e ->
                    Log.e(activity.javaClass.simpleName,"Error writing documentation", e)
                }
    }

    fun createBoard(activity: CreateBoardActivity, board: Board) {
        mFireStore.collection(Constants.BOARDS)
                .document()
                .set(board, SetOptions.merge())
                .addOnSuccessListener {
                    Log.e(activity.javaClass.simpleName, "Board created successfully")
                    Toast.makeText(activity, "Board created successfully.", Toast.LENGTH_SHORT).show()
                    activity.boardCreatedSuccessfully()
                }.addOnFailureListener {
                    exception ->
                    activity.hideProgressDialog()
                    Log.e(activity.javaClass.simpleName, "Error while creating a board.", exception)
                }
    }

    // Firebase에  Key-Value 형식의 데이터로 업이트.
    fun updateUserProfileData(activity: MyProfileActivity, userHashMap: HashMap<String, Any>) {
        mFireStore.collection(Constants.USER)
                .document(getCurrentUserID())
                .update(userHashMap)
                .addOnSuccessListener {
                    Log.i(activity.javaClass.simpleName, "Profile Data updated Successfully!")
                    Toast.makeText(activity, "Profile Data updated Successfully!", Toast.LENGTH_SHORT).show()
                    activity.profileUpdateSuccess()
                }.addOnFailureListener { e ->
                    activity.hideProgressDialog()
                    Log.e(activity.javaClass.simpleName,
                    "Error while creating a board.", e)
                    Toast.makeText(activity, "Error when profile update", Toast.LENGTH_SHORT).show()
                }
    }

    // Sign In
    fun loadUserData(activity: Activity){
        mFireStore.collection(Constants.USER)
                .document(getCurrentUserID())
                .get()
                .addOnSuccessListener { document ->
                    document.toObject(User::class.java)?.also {
                        Log.e("ACTIVITY", "${activity.localClassName}")
                        when (activity) {
                            is SignInActivity -> {
                                activity.signInSuccess(it)
                            }
                            is MainActivity -> {
                                activity.updateNavigationUserDetails(it)
                            }
                            is MyProfileActivity -> {
                                activity.setUserDataInUI(it)
                            }
                        }
                    }
                }.addOnFailureListener { e->
                    Log.e("SigInUser", "Error writing document", e)
                }
    }

    fun getCurrentUserID(): String {
        var currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""
        if (currentUser != null){
            currentUserID = currentUser.uid
        }
        return currentUserID
    }
}