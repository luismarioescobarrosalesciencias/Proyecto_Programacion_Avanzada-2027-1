import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Dimensionar{

    public static void main(String[] args ){
        String nombre = args[0];
        String linea ;
        String[] campos;
        File f1  = new File(nombre);
        int filas  = 0 ;
        int columnas = 0 ;
        try{
            BufferedReader archioOrigen = new BufferedReader(new FileReader(f1));
            while((linea = archioOrigen.readLine())!= null ){
                
                filas++;
               // System.out.println(linea); //Imprime toda la linea
                if(columnas == 0 ){ 
                    campos = linea.split(","); // Segmenta los campos por el separador de campo establecido
                    columnas = campos.length;
                }
            }
    
            

            System.out.println("Salida: ");

            System.out.println("Filas: "+ (filas-1)); //No se cuenta la cabecera 

            System.out.println("Columnas: "+columnas);

        }catch (IOException e){
            System.out.println("Ocurrio un error ");

        }

       //usar character stream  
       // System.out.println("Nombre: "+ nombre);
    }

}