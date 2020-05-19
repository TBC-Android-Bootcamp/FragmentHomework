package com.example.fragmentz

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_dashboard.view.*
import org.json.JSONObject

/**
 * A simple [Fragment] subclass.
 */
class DashboardFragment(private val userId:Int? = null, private val email:String? = null) : Fragment() {

    private lateinit var mContext: Context

    private lateinit var mView:View
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_dashboard, container, false)
        setData(userId)

        return mView
    }

    private fun setData(userId: Int?) {
        if (userId != null) {
            HttpRequest.getSingleUserRequest(
                userId,
                object : HttpRequest.RequestCallBacks {
                    override fun onSuccess(successJsonString: String) {
                        val jsonObject = JSONObject(successJsonString)
                        val dataObject = jsonObject.getJSONObject("data")
                        updateUiWithDataJson(dataObject)
                    }

                    override fun onFailure(failureMessage: String) {
                        Toast.makeText(mContext, failureMessage, Toast.LENGTH_LONG).show()
                    }

                })
        } else if (email != null) {
            mView.emailTextView.text = email
            mView.cardView.visibility = View.GONE
        }
    }


    private fun updateUiWithDataJson(dataObject: JSONObject) {
        mView.emailTextView.text = dataObject.getString("email")
        mView.firstNameTextView.text = dataObject.getString("first_name")
        mView.lastNameTextView.text = dataObject.getString("last_name")

        Picasso.with(mContext).load(dataObject.getString("avatar"))
            .resize(92, 92)
            .into(mView.userImageView, object : Callback {
                override fun onSuccess() {
                    val imageBitmap =
                        (mView.userImageView.drawable as BitmapDrawable).bitmap
                    val imageDrawable =
                        RoundedBitmapDrawableFactory.create(
                            resources,
                            imageBitmap
                        )
                    imageDrawable.isCircular = true
                    imageDrawable.cornerRadius =
                        imageBitmap.width.coerceAtLeast(imageBitmap.height) / 2.0f
                    mView.userImageView.setImageDrawable(imageDrawable)
                }

                override fun onError() {
                    mView.userImageView.setImageResource(R.mipmap.ic_launcher)
                }
            })
    }

}
