package apwidgets;

import android.os.Bundle;
import processing.core.PApplet;

public class APActivity extends PApplet{
	public FakePApplet fakePApplet;
	
	public Object createObject(String className) {
	      Object object = null;
	      try {
	          Class classDefinition = Class.forName(className);
	          try {
				object = classDefinition.newInstance();
			} catch (java.lang.InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	      } catch (InstantiationException e) {
	          System.out.println(e);
	      } catch (IllegalAccessException e) {
	          System.out.println(e);
	      } catch (ClassNotFoundException e) {
	          System.out.println(e);
	      }
	      return object;
	   }
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		fakePApplet = (FakePApplet)createObject(getActivity().getIntent().getStringExtra("fakePApplet"));
	}
	
	public void setup(){
		if(fakePApplet!=null){
			fakePApplet.setup();
		}
	}
	
	public void draw(){
		rect(78,78,78,78);
		if(fakePApplet!=null){
			ellipse(10,10,10,10);
			fakePApplet.draw();
		}
	}
}
