package com.gigigo.imagerecognition

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.gigigo.imagerecognition.vuforia.ContextProvider
import com.gigigo.imagerecognition.vuforia.ImageRecognitionVuforia

class ImageRecognizerActivity : AppCompatActivity() {


  private val asvlicenseKey="AVF6Bi3/////AAAAmSHLdJe3ZUZrgF1Y5ckGTp07SkzjR9YT4Qk8ObwDTd8CYhZVdORRHtXUdAS/4HPnuu2mS+SxH1qG/gfDwVTOjcwfVtwW1fpzunmJ349wfW/SmRBA4EaWfqaU5eFQV1/wiMvaOmUuLc41TWKiuknpn8IVKtReE/uX67YiFtRt6GRRsp5DtMgseoC0rseFYujicHuGCnoRY/KB/ew2aaCQ8DJTcjIu7I6qHpizXsqBfH0EmHiDaMLgHBIBoxHqPqvo2W4fj8NIL47cBInSzpmjlI3N1SaJqGNFaI0A+8yv9HwOGGCDOVFQ0BPhU1U+y9QaMRbDnS8skxIPuVphEzqQm/tnYz+YW/SGDuidJYTq/Auy";
  private val asvAccessKey: String = "efac882b322980f0959b72364d2e27eb4c402e12"
  private val asvSecretKey: String = "dd3967639fe37ad9adf1060d8d274cebb0b226fb"

//  private val licenseKey: String = "Abi5nL//////AAAAAXzdWR6MxEppinKbZ9qJjhU7Op5/+8Pwm8tdYfI4f3zFmRweYqowENwgiOUAtaiIH06OpQFISbhX9Linf/uq5JXUADO/MFrnbzy/UIuA3whurbD+Q18bV3uRrm2FtvF64fWdH7R1GoAbOEL6wbF621Da0JJ4uVYAZEYOga/6C4fBEtf0LpKoetdNIVpIxvWsIWHRNVWX41gbRTmwSqCnoV1axtSqBAalAx5Oq/GjoD4a8isoBRJMhkIEOR+4Q7lbyJrQatD+9TqINi9wAuBY9/atNKA27AzMpnQcuAaSr2rv8Y8r3wtk7yQY7oTm8CrBMLri+TdEZoF6Z/TdZaupRaqrlKZqtptOme0zoodbOTVe"
//  private val clientAccessKey: String = "fe4d316136ea6b7ee5faa72c4884e33805128b08"
//  private val clientSecretKey: String = "670f15bb4cd34c1621a892ced5321896c0b70df6"

  //patxi license
//  private val LICENSE_KEY = "AYQlF+z/////AAAAmZNxjAfBiUzJpd4uNBnJf4oQoQUu+r+qGO3ASpGlO3UKUUuBQNGeQVgrJwPrNKRN8EAiwtXj114QEc+1TkF1RJ7V71u2wUuiMI1ujJBnIyGVHqYLG9/poCuChR0pMrj1HSyUzH91HXKQQKT4739+anOWvK60Vobedg+K2cg2hFwfXj9n5jeU6nNxlLwagevf7+mDGR716bX5/rMgyRnnp+VXPwuJe8ZrVS8VsS2UUtezLcxyl9ZJ7sGcrT8F4nSkuccmF0zA1yG+hisPQvUb73hn1EZqQjzKpz+hr34LAfVi1IKWNvqDSmepPO1q5iwCUuudu8bunh4QMPO16YMyMzZsK5H+FnHzSAGVc9NBj3zZ"
//  private val kAccessKey = "2675f5fd69af0cdae84e7f00728d063ed1c0f16b"
//  private val kSecretKey = "ae194c709ddd667bf466e1cf47cbb019027b2c99"




  private lateinit var licenseKeyTv: TextView
  private lateinit var accessKeyTv: TextView
  private lateinit var secretKeyTv: TextView
  private lateinit var codeTv: TextView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_imagerecognizer)

    initViews()

//    licenseKeyTv.text = "license key: ${licenseKey}"
//    accessKeyTv.text = "access key: ${clientAccessKey}"
//    secretKeyTv.text = "secret key: ${clientSecretKey}"

//    licenseKeyTv.text = "license key: ${LICENSE_KEY}"
//    accessKeyTv.text = "access key: ${kAccessKey}"
//    secretKeyTv.text = "secret key: ${kSecretKey}"

    licenseKeyTv.text = "license key: ${asvlicenseKey}"
    accessKeyTv.text = "access key: ${asvAccessKey}"
    secretKeyTv.text = "secret key: ${asvSecretKey}"
  }

  private fun initViews() {
    initToolbar()

    licenseKeyTv = findViewById(R.id.imagerecognizer_license_tv) as TextView
    accessKeyTv = findViewById(R.id.imagerecognizer_accessKey_tv) as TextView
    secretKeyTv = findViewById(R.id.imagerecognizer_secretKey_tv) as TextView
    codeTv = findViewById(R.id.imagerecognizer_code_tv) as TextView

    val startButton = findViewById(R.id.start_vuforia_button) as Button
    startButton.setOnClickListener({ startVuforia() })
  }

  private fun initToolbar() {

    val toolbar = findViewById(R.id.toolbar) as Toolbar

    setSupportActionBar(toolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(false)

    toolbar.setNavigationOnClickListener { onBackPressed() }

    title = getString(R.string.imagerecognizer_title)
  }

  private fun showResponseCode(code: String) {
    try {
      Toast.makeText(this, "CODE= $code", Toast.LENGTH_LONG).show()
       //codeTv.text = "CODE= $code"
    } catch (th: Throwable ) {
      System.out.println(th.toString());
    }
  }

  private fun startVuforia() {
    var imageRecognition = ImageRecognitionVuforia()

    ImageRecognitionVuforia.onRecognizedPattern {
      showResponseCode(it)
    }


    val contextProvider = object : ContextProvider {
      override fun getCurrentActivity(): Activity = this@ImageRecognizerActivity
      override fun isActivityContextAvailable(): Boolean = true
      override fun getApplicationContext(): Context = this@ImageRecognizerActivity.application.applicationContext
      override fun isApplicationContextAvailable(): Boolean = true
    }
    imageRecognition.setContextProvider(contextProvider)

    val vuforiaCredentials = object : Credentials {
//      override fun getLicensekey(): String = this@ImageRecognizerActivity.licenseKey
//      override fun getClientAccessKey(): String = this@ImageRecognizerActivity.clientAccessKey
//      override fun getClientSecretKey(): String = this@ImageRecognizerActivity.clientSecretKey


//      override fun getLicensekey(): String = this@ImageRecognizerActivity.LICENSE_KEY
//      override fun getClientAccessKey(): String = this@ImageRecognizerActivity.kAccessKey
//      override fun getClientSecretKey(): String = this@ImageRecognizerActivity.kSecretKey

      override fun getLicensekey(): String = this@ImageRecognizerActivity.asvlicenseKey
      override fun getClientAccessKey(): String = this@ImageRecognizerActivity.asvAccessKey
      override fun getClientSecretKey(): String = this@ImageRecognizerActivity.asvSecretKey
    }
    imageRecognition.startImageRecognition(vuforiaCredentials)
  }
}