package mx.edu.ittepic.ladm_u3_practica2_renteriareyes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.BoringLayout
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var baseRemota =  FirebaseFirestore.getInstance()
    var existe = false
    var lista = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        /*---------------------- BOTON PARA GUARDAR LA DATA--------------------------------*/
        btnGuardar.setOnClickListener {
            /*--------------------------------- VERIFICAR SI EL CLIENTE EXISTE (CON SU NUM DE TELEFONO)------------------------------------*/
            baseRemota.collection("restaurante").whereEqualTo("celular",txtCelular.text.toString()).get().addOnSuccessListener {
                for(document in it){
                    lista.add(document.getString("celular")!!)
                }
                if(lista.size == 0){
                    guardarPedido()
                    lista.clear()
                }else{
                    if(lista.size > 0){
                        Toast.makeText(this, "EL CLIENTE YA TIENE PEDIDOS", Toast.LENGTH_SHORT).show()
                        lista.clear()

                    }else {
                        //Toast.makeText(this, "PUEDES INGRESAR TU PEDIDO", Toast.LENGTH_SHORT).show()
                        guardarPedido()
                        lista.clear()
                    }
                }

            }.addOnFailureListener {
                Toast.makeText(this, "ERROR", Toast.LENGTH_LONG).show()

            }

        }
        btnConsultar.setOnClickListener {
            startActivity(Intent(this, Main2Activity:: class.java))
        }

    }
    /*--------------------------- GUARDAR DATA--------------------------------------------------------*/
    private fun guardarPedido() {
        /*------------------------------------ RECUPERAR DATA----------------------------------*/
        var entregado = ""
        if (radioTrue.isChecked){
            entregado = "true"
        }
        if (radioFalse.isChecked){
            entregado = "false"
        }
        var data = hashMapOf(
            "nombre" to txtNombre.text.toString(),
            "domicilio" to txtDomicilio.text.toString(),
            "celular" to txtCelular.text.toString(),
            "pedido" to hashMapOf(
                "cantidad" to txtCantidad.text.toString().toInt(),
                "entregado" to entregado,
                "precio"    to txtPrecio.text.toString().toFloat(),
                "producto" to txtProducto.text.toString()
            )
        )
            baseRemota.collection("restaurante")
                .add(data)
                .addOnSuccessListener {
                    Toast.makeText(this, "PEDIDO GUARDADO", Toast.LENGTH_LONG).show()
                    lista.clear()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "ERROR NO SE CAPTURÓ", Toast.LENGTH_LONG).show()

                }

    }//guardarpedido




}
