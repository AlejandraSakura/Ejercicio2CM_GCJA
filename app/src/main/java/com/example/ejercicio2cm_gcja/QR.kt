package com.example.ejercicio2cm_gcja

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.provider.ContactsContract
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.zxing.Result
import com.google.zxing.client.result.*
import me.dm7.barcodescanner.zxing.ZXingScannerView
import java.net.MalformedURLException
import java.net.URL
import java.lang.Exception
import java.net.URISyntaxException

class QR : AppCompatActivity(), ZXingScannerView.ResultHandler {
    private val PERMISO_CAMARA = 1
    private var scannerView: ZXingScannerView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        scannerView = ZXingScannerView(this@QR)
        setContentView(scannerView)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checarPermiso()){
                //Se concedio el permiso
            }else{
                solicitarPermiso()
            }

        }

        scannerView?.setResultHandler(this@QR)
        scannerView?.startCamera()
    }

    private fun solicitarPermiso() {
        ActivityCompat.requestPermissions(this@QR, arrayOf(Manifest.permission.CAMERA), PERMISO_CAMARA )
    }

    private fun checarPermiso(): Boolean {
        return (ContextCompat.checkSelfPermission(this@QR, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
    }

    override fun handleResult(p0: Result?) {
        //Código QR leído
        val scanResult = p0?.text
        Log.d("QR_LEIDO",scanResult!!)
///////////////////////////////////////////////////////////////////////////////////////////////////
//                      QR URL
        try {
            val url =URL(scanResult)
            val i = Intent(Intent.ACTION_VIEW)
            i.setData(Uri.parse(scanResult))
            startActivity(i)
            finish()
        }catch (e : MalformedURLException){
            AlertDialog.Builder(this@QR)
                .setTitle(R.string.Error)
                .setMessage(R.string.CodigoInvalido)
                .setPositiveButton(R.string.Aceptar,DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                    finish()
                })
                .create()
                .show()
        }

        //                      QR SMS
        try {
            val list = scanResult.split(":")
            var smsi = Intent(Intent.ACTION_VIEW)
            smsi.setData(Uri.parse("smsto:"+list[1]))
            smsi.putExtra("sms_body",list[2])
            startActivity(smsi)
            finish()

        }catch(e:Exception) {
            AlertDialog.Builder(this@QR)
                .setTitle(R.string.Error)
                .setMessage(R.string.CodigoInvalido)
                .setPositiveButton(
                    R.string.Aceptar,
                    DialogInterface.OnClickListener { dialogInterface, i ->
                        dialogInterface.dismiss()
                        finish()
                    })
                .create()
                .show()
        }
///////
        //                      QR MAIL
        try{
            //-------------------------------------------------------------------
//            for email intent
            val listEmail = scanResult.split(":")
            var ei = Intent(Intent.ACTION_VIEW)
            ei.setData(Uri.parse("mailto:"))
            ei.putExtra(Intent.EXTRA_EMAIL,listEmail[2])
            ei.putExtra(Intent.EXTRA_SUBJECT,listEmail[4])
            ei.putExtra(Intent.EXTRA_TEXT,listEmail[6])
            startActivity(ei)
            finish()


        }catch(e: Exception){
            AlertDialog.Builder(this@QR)
                .setTitle(R.string.Error)
                .setMessage(R.string.CodigoInvalido)
                .setPositiveButton(
                    R.string.Aceptar, DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                    finish()
                })
                .create()
                .show()
        }



///////
    }
    ///////////////////////////////////////////////////////////////////////////////////////
    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checarPermiso()){
                if (scannerView == null){
                    scannerView = ZXingScannerView(this@QR)
                    setContentView(scannerView)
                }
                scannerView?.setResultHandler(this@QR)
                scannerView?.startCamera()
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        scannerView?.stopCamera()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            PERMISO_CAMARA -> {
                if (grantResults.isNotEmpty()){
                    if(grantResults[0]!=PackageManager.PERMISSION_GRANTED){
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                            if(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)){
                                AlertDialog.Builder(this@QR)
                                    .setTitle(R.string.PermisoRequerido)
                                    .setMessage(R.string.AccesoCamara)
                                    .setPositiveButton(R.string.Aceptar, DialogInterface.OnClickListener { dialogInterface, i ->
                                        requestPermissions(arrayOf(Manifest.permission.CAMERA),PERMISO_CAMARA)
                                    })
                                    .setNegativeButton(R.string.Cancelar, DialogInterface.OnClickListener { dialogInterface, i ->
                                        dialogInterface.dismiss()
                                        finish()
                                    })
                                    .create()
                                    .show()
                            }else {
                                Toast.makeText(this@QR, R.string.NoAcceso,Toast.LENGTH_LONG).show()
                                finish()
                            }
                        }
                    }
                }

            }

        }

    }


}