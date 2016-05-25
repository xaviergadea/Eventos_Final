package es.pue.eventos.model.persistencelayer.impl.rest.daos;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import es.pue.eventos.model.businesslayer.entities.Evento;
import es.pue.eventos.model.businesslayer.entities.Participante;
import es.pue.eventos.model.persistencelayer.api.IEventoDAO;
import es.pue.eventos.presentationlayer.broadcastreceivers.RestEventBroadcastReciever;

/**
 * Created by android-ed1 on 23/05/2016.
 */
public class EventoDao implements IEventoDAO {

    Context context;

    public EventoDao(Context context){
        this.context=context;
    }

    @Override
    public void eventosSave(List<Evento> eventos) throws JSONException {
        new SaveEvento().execute(eventos);
    }

    @Override
    public Evento getEventoByDorsal(String dorsal) {

        List<Evento> eventos=getEventosFromRest();

        Evento evento=null;
        if(eventos!=null) {
            for (Evento e : eventos) {
                for (Participante inscrito : e.getInscritos()) {
                    if (inscrito.getDorsal().equals(dorsal)) {
                        e.setInscritos(new ArrayList<Participante>());
                        e.getInscritos().add(inscrito);
                        evento = e;
                        break;
                    }
                }
            }
        }
        return  evento;

    }

    private List<Evento> getEventosFromRest(){
        List<Evento> result=null;
        try{
            URL url=new URL("http://api.myjson.com/bins/3tyc8");
            HttpURLConnection connection=(HttpURLConnection)url.openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-length","0");
            connection.setUseCaches(false);
            connection.setAllowUserInteraction(false);
            connection.connect();

            int status=connection.getResponseCode();
            switch(status){
                case 200:
                case 201:
                    BufferedReader reader=new BufferedReader(new InputStreamReader(
                            connection.getInputStream()
                    ));
                    StringBuilder builder=new StringBuilder();
                    String line;
                    while((line=reader.readLine())!=null){
                        builder.append(line);
                    }
                    Log.i("EventoDao REST","Result getting all eventos: "+builder);
                    Gson gson=new Gson();
                    result=gson.fromJson(builder.toString(),
                            new TypeToken<List<Evento>>(){}.getType());



                    break;
            }

        }catch(Exception e){
            Log.e("EventoDao REST","Error getting evento "+e.getMessage());
            e.printStackTrace();
        }

        return result;

    }

    class SaveEvento extends AsyncTask<List<Evento>, Void,Boolean>{

        @Override
        public Boolean doInBackground(List<Evento>... params){

            Boolean result=false;
            URL url=null;

            try{
                url=new URL("http://api.myjson.com/bins/3tyc8");
                HttpURLConnection connection=(HttpURLConnection)url.openConnection();
                connection.setRequestMethod("PUT");
                connection.setRequestProperty("Content-Type","application/json");
                connection.setDoOutput(true);
                connection.setDoInput(true);

                connection.connect();

                Gson gson= new Gson();
                String json=gson.toJson(params[0]);

                OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
                wr.write(json);
                wr.flush();
                wr.close();//dubtes

                int responseCode=connection.getResponseCode();
                final StringBuilder builder=new StringBuilder("Requested URL: "+url);
                BufferedReader br=new BufferedReader(
                        new InputStreamReader(
                                connection.getInputStream()
                        )
                );

                String line=null;
                while((line=br.readLine())!=null){
                    builder.append(line);
                }
                Log.i("EventoDao REST","Result saving eventos "+builder);
                br.close();
                if(responseCode==HttpURLConnection.HTTP_OK) result=true;
            }catch (Exception e){
                e.printStackTrace();
            }

            Intent intent= new Intent();
            intent.setAction("es.pue.eventos.Broadcast");
            intent.putExtra(RestEventBroadcastReciever.REST_RESULT_EXTRA,result);
            Log.i("Rest EventoDAO","Sending Broadcast");
            context.sendBroadcast(intent);

            return result;
        }

    }

}
