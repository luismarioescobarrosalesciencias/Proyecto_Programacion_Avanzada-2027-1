import java.io.File;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.HashSet;

public class Inventario{

    //metodo iterativo 
    public LinkedList<String> recorrido(File raiz){
        //Set  <String> visitados =  new HashSet <>();
        LinkedList  <String> caminos =  new LinkedList <>();
        Deque <File> pila = new ArrayDeque<>();
        //validar que raiz sea una carpeta 
        pila.push(raiz);
        File current ;
        String camino =  "";
        int count = 0;
        while(!pila.isEmpty() ){
            //System.out.println(pila.toString());
            current =  pila.pop();
            camino+= current.getName();
            File[] listado = current.listFiles();
            for(File s : listado){
                if(s.isFile()){
                    // se guarda todo el camino en la lista , es como si fuera una hoja 
                    caminos.add(camino+File.separator+s.getName());
            }
            else if(current.isDirectory()){
                camino+=File.separator;
                pila.push(s);
            }

            }
            count++;
        }
        return caminos;

    }


      public static void main(String[] args ){
        File raiz =  new File(args[0]);
        if (!raiz.isDirectory()){
            System.out.println("Entrada no valida");
            System.exit(-1);
        }

        Inventario inventario =  new Inventario();
        LinkedList <String> caminos =  inventario.recorrido(raiz);
        for(String camino: caminos){
            System.out.println(camino);
            //System.out.println("\n")
        }

      }



}