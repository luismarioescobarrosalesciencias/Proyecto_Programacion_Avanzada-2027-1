import java.io.File;
import java.io.IOException;
public class Arbol {

    public void   dfs (File raiz,String identacion){
    if(!raiz.exists() ) {
        return ; }

        
     File[] listado = raiz.listFiles();
        for(File s : listado){
            if(s.isDirectory()){
                System.out.println(identacion+"->"+s.getName()+"");
                dfs(s,identacion+"  ");
            }else{
                System.out.println(identacion+"-"+s.getName());
            }

            
        }
         


    }


    public static void main(String[] args ){
        String rutaDir =  args[0];
        String separador =  File.separator;
        //usar list para sacar a los nombres de archivos y directorios
        //listarlos , y hacer  dfs sobre los directorios 
        String[] path ;
        //path  = rutaDir.split(separador);
 

        File raiz  =  new File (rutaDir);
        if(!raiz.isDirectory()){
            System.out.println("No es una ruta de Directorio valida"); 
            //termina ejecucion
            System.exit(1); 
        }
        Arbol a1 =  new Arbol();
        a1.dfs(raiz,"");

    }
    
}