import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Coincidencias{
  
//NOta aqui usamos regex para las coincidencias 

    public void   dfs (File raiz,Pattern patron){
    if(!raiz.exists() ) {
        return ; }

        
     File[] listado = raiz.listFiles();
        for(File s : listado){
            // comparacion de getname con regex 
            // si coincide que lo imprima 
            Matcher buscador = patron.matcher(s.getName());
            if(buscador.find()){
                System.out.println(s.getName());
            }

            if (s.isDirectory()){
                dfs(s,patron);
            }


            
        }
    }
        
        
    public static void main(String[] args ){
            String archivo =  args[0];    
            String rutaDir =  args[1];
            //String separador =  File.separator;
       
            String[] path ;

            File raiz  =  new File (rutaDir);
            if(!raiz.isDirectory()){
                System.out.println("No es una ruta de Directorio valida"); 
                System.exit(1); 
            }
            Coincidencias a1 =  new Coincidencias();
            Pattern patron = Pattern.compile(archivo);
            a1.dfs(raiz,patron);

    } 
    
}