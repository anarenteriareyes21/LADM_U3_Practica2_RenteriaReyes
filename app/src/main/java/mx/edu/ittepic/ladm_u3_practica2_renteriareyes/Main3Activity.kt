package mx.edu.ittepic.ladm_u3_practica2_renteriareyes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main3.*
import kotlinx.android.synthetic.main.activity_main3.view.*

class Main3Activity : AppCompatActivity() {
    var id = ""
    var listaNumeros = ArrayList<String>()
    var celularCambia = ""
    /*-------------- OBTENER BD REMOTA-------------*/
    var baseRemota = FirebaseFirestore.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)
        /*--------------- RECUPERAR DATA ----------------------*/
        var extras = intent.extras
        id = extras?.getString("id").toString()
        txtANombre.setText(extras?.getString("nombre"))
        txtACelular.setText(extras?.getString("celular"))
        celularCambia = extras?.getString("celular").toString()
        txtADomicilio.setText(extras?.getString("domicilio"))
        txtACantidad.setText(extras?.getString("cantidad"))
        if (extras?.getString("entregado") == "false") {
            radioAFalse.isChecked = true
        }
        if (extras?.getString("entregado") == "true") {
            radioATrue.isChecked = true
        }
        txtAPrecio.setText(extras?.getString("precio"))
        txtAProducto.setText(extras?.getString("producto"))
        /*-----------------------------------------------------------------*/

        /*-------------------------- BOTON PARA ACTUALIZAR DATA---------------------*/
        btnActualizar.setOnClickListener {
            /*----------------------- VERIFICAR SI EXISTE EL CLIENTE (CON SU NUM DE CELULAR) ----------------------*/
            baseRemota.collection("restaurante").whereEqualTo("celular",txtACelular.text.toString()).get().addOnSuccessListener {
                for(document in it){
                    listaNumeros.add(document.getString("celular")!!)
                }
                if(listaNumeros.size > 0){
                    if(celularCambia == txtACelular.text.toString()){
                        //ES EL MISMO CLIENTE
                        actualizarDatos()
                        listaNumeros.clear()
                    }
                    else{
                        //EL NUMERO DE TELEFONO YA EXISTE EN LA BASE DE DATOS
                        Toast.makeText(this, "ESTE CLIENTE YA EXISTE", Toast.LENGTH_SHORT).show()
                        listaNumeros.clear()
                    }
                }else {
                    //EL NUMERO NO EXISTE EN LA BD, POR LO CUAL SE PUEDE ACTUALIZAR
                    actualizarDatos()
                    listaNumeros.clear()
                }

            }.addOnFailureListener {
                Toast.makeText(this, "ERROR", Toast.LENGTH_LONG).show()

            }

        }//btnActualizar

        /*--------------------------- BOTON REGRESAR SIN ACTUALIZAR-------------------*/
        btnRegresar.setOnClickListener {
            finish()
        }

    }
    /*-------------------------- ACTUALIZAR DATA---------------------------------*/
    fun actualizarDatos(){
        /*------------- ACTUALIZAR DATA DE BD REMOTA----------------------*/
        var entregado = ""
        if (radioATrue.isChecked) {
            entregado = "true"
        }
        if (radioAFalse.isChecked) {
            entregado = "false"
        }
        baseRemota.collection("restaurante")
            .document(id)
            .update(
                "nombre", txtANombre.text.toString(),
                "celular", txtACelular.text.toString(),
                "domicilio", txtADomicilio.text.toString(),
                "pedido.cantidad", txtACantidad.text.toString().toInt(),
                "pedido.entregado", entregado,
                "pedido.precio", txtAPrecio.text.toString().toFloat(),
                "pedido.producto", txtADomicilio.text.toString()
            )
            .addOnSuccessListener {
                Toast.makeText(this, "ACTUALIZACIÓN REALIZADA", Toast.LENGTH_LONG).show()
                celularCambia = ""
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(
                    this,
                    "ERROR NO SE PUEDE ACTUALIZAR, NO HAY FORMA DE CONECTARSE A LA BD",
                    Toast.LENGTH_LONG
                ).show()
                celularCambia = ""

            }
    }
}
