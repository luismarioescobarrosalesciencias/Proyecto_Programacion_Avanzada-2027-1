import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;


public class Dividir {

    public boolean validar(File archivo){
        String err  =  "La entrada "+archivo.getName()+" ";
        if(!archivo.exists()){
            System.out.println(err+"no existe");
            return false;
        }

        if(!archivo.canRead()){
            System.out.println(err+"no puede leerse");
            return false;
        }

        if (!archivo.isFile()){
            System.out.println(err+"no es un archivo");
            return false;
        }
        
        return true;

    }

    public void dividir (File archivo,int n)throws IOException{
        if(!validar(archivo))return;
        String prefijo =  "archivo_";
        int postfijo =  1 ;

        //Por cada que se termine de leer una linea se crea un archivo
        try{
            BufferedReader lectura =  new BufferedReader(new FileReader(archivo));
            //BufferedWriter resultado =  new BufferedWriter(new FileWriter())
            String linea ;
            BufferedWriter resultado  =  null ;
            while((linea = lectura.readLine()) != null   &&  postfijo <= n){
                //System.out.println(linea);
                String nombre  = prefijo +  String.valueOf(postfijo)+".txt";
                File  archivo_nuevo =  new File (nombre);
                if (archivo_nuevo.exists()) 
                     archivo_nuevo.delete(); //Suponiendo que se pueda eliminar 

                resultado  =  new BufferedWriter(new FileWriter(nombre));
                resultado.write(linea);
                resultado.close();
                postfijo++;


            }
        }catch (IOException e) {
            System.err.println("Error de entrada/salida: "
                               + e.getMessage());

        }

        /** 
        if(postfijo < n ){
            //Si todavia hay contendio en el archivo que deberia suceder ?
            //guardar  todo en el ultimo archivo o simplemente quedarse hasta donde se indique?

        }**/


    }

    public static void main(String[] args ){



        if (args.length != 2) {
            System.err.println("Uso: java Filtrar <archivo> <patron>");
            System.exit(-1);
        }

        // Nombre del archivo
        // Patrón de información
        File archivo = new File(args[0]);
        int numero = Integer.parseInt(args[1]);

        Dividir programa = new Dividir();

        try {
            programa.dividir(archivo, numero);

        } catch (IOException e) {
            System.err.println("Error de entrada/salida: "
                               + e.getMessage());


        }

    }
}