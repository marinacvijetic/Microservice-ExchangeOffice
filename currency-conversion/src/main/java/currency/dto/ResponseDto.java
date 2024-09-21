package currency.dto;

public class ResponseDto<S, T> {

	private S message;
	private T bankAccountData;
	
	public ResponseDto(S message, T data) {
		this.message = message;
		this.bankAccountData = data;
		
	}
	
	public S getMessage() {
		return message;
	}
	
	public T getData() {
		return bankAccountData;
	}
}
