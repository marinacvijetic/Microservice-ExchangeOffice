package cryptoConversion.dto;

public class ResponseDto<S, T> {

	private S message;
	private T cryptoWalletData;
	
	public ResponseDto(S message, T data) {
		this.message = message;
		this.cryptoWalletData = data;
	}
	 
	public S getMessage() {
		return message;
	}
	
	public T getData() {
		return cryptoWalletData;
	}
}
