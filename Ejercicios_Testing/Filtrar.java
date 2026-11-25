import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class Filtrar{

    
    private boolean validacion(File archivo){
        //Verificar que el archivo exista , se pueda leer etc
        String err  = "La entrada "+archivo.getName()+" ";
        int i =  10;
        if(!archivo.exists()){
              System.out.println(err+"no existe");
              return false;
            }

        if(!archivo.canRead()){
              System.out.println(err+"no puede leerse");
              return false;
        }

        if(!archivo.isFile())  {
              System.out.println(err+"no es un archivo");
              return false;
        }
        

        return true;


    }
    
    public void  filtro(File archivo,String patron_cadena )throws IOException{
        if(!validacion(archivo)) return ;
        Pattern patron  = Pattern.compile(patron_cadena);
        //Matcher matcher = patron.matcher
        //lectura de archivo
        try(
        BufferedReader lectura =  new BufferedReader(new FileReader(archivo));
        // Archivo de Escritura 
        //File resultado  =  new File("resultado.txt");
        BufferedWriter resultado  = new BufferedWriter(new FileWriter("resultado.txt"))
        ){
        Matcher  match;
        String linea;
        while( (linea = lectura.readLine()) != null){
            // si la linea coincide se escribe en el archivo nuevo , escribir en resultado 
            match  =  patron.matcher(linea);
            if(match.find()){
                //escribir en resultado  
                resultado.write(linea);
                resultado.newLine();
            }
        }

        }
        
        // cerrar archivos 
        //lectura.close();
        //resultado.close();

    }


    public static void main(String[] args ){

        // Nombre del archivo
        // Patron de informacion
        if (args.length != 2) {
            System.err.println("Uso: java Filtrar <archivo> <patron>");
            System.exit(-1);
        }

        // Nombre del archivo
        // Patrón de información
        File archivo = new File(args[0]);
        String patron = args[1];

        Filtrar programa = new Filtrar();

        try {
            programa.filtro(archivo, patron);

        } catch (IOException e) {
            System.err.println("Error de entrada/salida: "
                               + e.getMessage());

        } catch (PatternSyntaxException e) {
            System.err.println("Expresión regular inválida: "
                               + e.getDescription());
        }
    }

        
}