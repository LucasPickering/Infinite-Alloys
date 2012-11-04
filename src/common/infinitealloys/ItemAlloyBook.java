package infinitealloys;

public class ItemAlloyBook extends ItemIA {

	public ItemAlloyBook(int id, int texture) {
		super(id, texture);
	}
	
	@Override
	public boolean getShareTag() {
		return true;
	}
}
