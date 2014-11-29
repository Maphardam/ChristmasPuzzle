import javax.swing.JLabel;

public class ChristmasLabel { 

	private JLabel label;
	private int position;

	public ChristmasLabel(JLabel label, int pos){
		this.label = label;
		this.position = pos;
	}
	
	protected int getPosition(){
		return this.position;
	}
	
	protected void setPosition(int pos){
		this.position = pos;
	}
	
	protected JLabel getLabel(){
		return this.label;
	}

}
