package com.gtech.gossipmessenger.data.entities

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {

    /******************************
     *         Getting Data       *
     ******************************/

    /**
     * Getting list of users [future use]
     */
    @Query("SELECT * FROM users")
    fun getAllUsers(): LiveData<List<SignIn>>

    /**
     * Getting default user [currently in use]
     */
    @Query("SELECT * FROM users WHERE id = 1")
    fun getDefaultUser(): LiveData<SignIn>

    /**
     * Getting user id [Current is statically passed]
     */
    @Query("SELECT tu_id FROM users WHERE id = 1")
    fun getUserId(): LiveData<Int>

    /******************************
     *         Saving  Data       *
     ******************************/

    /**
     * Saving new user data
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(signIn: SignIn)


    /******************************
     *         Updating Data      *
     ******************************/

    /**
     * Update cover picture
     */
    @Query("UPDATE users SET tu_cover_pic = :coverPicture WHERE id = 1")
    fun updateCoverPicture(coverPicture: String)

    /**
     * Update profile picture
     */
    @Query("UPDATE users SET tu_profile_pic = :profilePicture WHERE id = 1")
    fun updateProfilePicture(profilePicture: String)

    /**
     * Update profile
     */
    @Query("UPDATE users SET tu_first_name = :firstName, tu_last_name = :lastName, tu_bio = :bio WHERE id = 1 ")
    fun updateProfile(firstName: String, lastName: String, bio: String)

    /**
     * Update username
     */
    @Query("UPDATE users SET tu_username = :username WHERE id = 1")
    fun updateUsername(username: String)

    /**
     * Update mobile number
     */
    @Query("UPDATE users SET tu_mobile = :mobile WHERE id = 1")
    fun updateMobileNumber(mobile: String)

    /**
     * Update email address
     */
    @Query("UPDATE users SET tu_email = :email WHERE id = 1")
    fun updateEmailAddress(email: String)


    /**
     * Dropping table
     */
    @Query("UPDATE users SET status = :status")
    fun updateLogout(status: Boolean)

    /**
     * Updating username availability
     */
    @Query("UPDATE users SET can_change_username = :canChangeUsername, change_username_message = :msg WHERE id = 1")
    fun updateUsernameChangeStatus(canChangeUsername: Boolean, msg: String)
}