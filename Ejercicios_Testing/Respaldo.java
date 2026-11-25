
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.security.MessageDigest;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;


public class Respaldo{
   private  Set<String> visitados = new HashSet<>();


    private String obtenerMD5(File archivo){
        StringBuffer cadenaMD5 = new StringBuffer();
        FileInputStream archivoLectura; // cambiar por byte reader
        MessageDigest instanciaMD5; //clase para hasheos

        byte[] bloqueFirmado;
        byte[] bloqueBytes = new byte[512];
        int byteActual = 0;
        try {
        instanciaMD5 = MessageDigest.getInstance("MD5");
        archivoLectura = new FileInputStream(archivo);
        while ((byteActual = archivoLectura.read(bloqueBytes)) != -1) {
            instanciaMD5.update(bloqueBytes, 0, byteActual);
        }

        archivoLectura.close();
        bloqueFirmado = instanciaMD5.digest();
        for (int i = 0; i < bloqueFirmado.length; i++) {
            cadenaMD5.append(Integer .toString((bloqueFirmado[i] & 0XFF) + 0x100, 16).substring(1));
        }
        } catch (NoSuchAlgorithmException | IOException e) {
        System.err.println(e);
        }
        return cadenaMD5.toString();
    }


    public boolean respaldar(File fuente ,File destino ){
            // S es un archivo
            // destino es un directorio
            String name =  fuente.getName();
            int dato;
            InputStream f1 = null;
            OutputStream f2 =  null;

            boolean cierreCorrecto = true;

            try{
                f1 = new FileInputStream(fuente);
                File archivoDestino = new File(destino,name);
    //OutputStream f2  =  new FileOutputStream(destino+File.separator+name);

                // Evitar sobrescribir archivos existentes
                if (archivoDestino.exists()){
                    System.err.println("El archivo ya existe: " + archivoDestino);
                    return false;
                }

                f2  = new FileOutputStream(archivoDestino);
                while ((dato = f1.read()) != -1 ){
                    f2.write(dato);

                }
                    //f1.close();
                    //f2.close();

            }catch(IOException e ){
                System.err.println(e);
                return false;
            } finally {
            if (f1 != null) {
                try {
                    f1.close();
                } catch (IOException e) {
                    System.err.println(e);
                    cierreCorrecto = false;
                }
            }

            if (f2 != null) {
                try {
                    f2.close();
                } catch (IOException e) {
                    System.err.println(e);
                    cierreCorrecto = false;
                }
            }
        }

        return cierreCorrecto;
        }


    public void respaldoArbol(File origen, File destino){
        if (!(origen.isDirectory()) || !(destino.isDirectory())) {
            System.out.println("Alguno de los argumentos no es diectorio");
            return;
        }

        // verificar que destino no este dentro de origen o el dfs se hace infinito
        try {
            String rutaOrigen = origen.getCanonicalPath();
            String rutaDestino = destino.getCanonicalPath();

            String prefijo = rutaOrigen.endsWith(File.separator) ? rutaOrigen : rutaOrigen + File.separator;

            if (rutaOrigen.equals(rutaDestino) || rutaDestino.startsWith(prefijo)) {

                System.out.println("El destino es igual o está dentro del origen.");
                return;
            }

        } catch (IOException e) {
            System.err.println(e);
            return;
        }

        dfs(origen, destino);
        this.visitados = new HashSet<String>(); // Limpiamos los valores del diccionario
    }

    public void dfs(File raiz,File destino){

        File[] files  =  raiz.listFiles();
        if(files == null){
            System.out.println("Entrada no valida");
            return ;
        }
        for( File s: files ){

            if(s.isFile()){
                String currentHash =  this.obtenerMD5(s);
                if(!visitados.contains( currentHash)){

                    // respadar en la ruta destino
                    boolean r  = respaldar(s,destino);
                    if(r) visitados.add(currentHash);

                }

            }
                    // si es un directorio nos movemos
            else if(s.isDirectory()){
                        //crear directorio en donde estamos parados
                File archivoActual = new File(destino,s.getName()); //-----------------
                if (!(archivoActual.exists())){
                    boolean creado  =  archivoActual.mkdir();
                    if (creado)
                        dfs(s,archivoActual);
                    else
                        System.err.println("FALLO AL CREAR DIRECTORIO: " + archivoActual);
                }else{
                    if (archivoActual.isDirectory()){
                        dfs(s,archivoActual);
                    }else{
                        System.err.println("LA RUTA EXISTE Y NO ES DIRECTORIO: " + archivoActual);
                    }
                }
            }


        }

    }

    public static void main(String[] args ){
    if (args.length != 2){
        System.out.println("Entrada Invalida");
        System.exit(1);
    }

    File origen = new File(args[0]);
    File destino = new File(args[1]);

    Respaldo respaldo = new Respaldo();
    respaldo.respaldoArbol(origen, destino);

    }

}
