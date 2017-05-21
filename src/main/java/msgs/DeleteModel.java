package msgs;

/**
 * Created by the following students at the University of Antwerp
 * Faculty of Applied Engineering: Electronics and ICT
 * Janssens Arthur, De Laet Jan & Verhoeven Peter.
 **/
public class DeleteModel {
    //Send
    public String model_name;

    //Receive
    public boolean success;
    public String status_message;

    public DeleteModel(){}
    public DeleteModel(String model_name){
        this.model_name=model_name;
    }
    public DeleteModel(String model_name, boolean success, String status_message){
        this.model_name=model_name;
        this.success=success;
        this.status_message=status_message;
    }
}
