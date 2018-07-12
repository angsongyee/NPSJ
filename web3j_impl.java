import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.concurrent.ExecutionException;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

public class web3j_impl {
	public static void main(String [] args) throws TransactionException, Exception {
		HttpService httpService = new HttpService("https://ropsten.infura.io/WZsr3qvgLiX9OOz3MqGV");
		Web3j web3 = Web3j.build(httpService);
		Web3ClientVersion web3ClientVersion = web3.web3ClientVersion().sendAsync().get();
		String clientVersion = web3ClientVersion.getWeb3ClientVersion();
		System.out.println(clientVersion);
		
		///below to generate new walletfile
//		File file = new File("C:\\Users\\user\\ethereum\\keystore");
		String password = "password@123";
//		String fileName = WalletUtils.generateFullNewWalletFile(password,file);
//		System.out.println(fileName);
		
		//below is to load existing wallet from a file *i have stored one in the keystore folder in this project
		String path=new File("").getAbsolutePath();
		String fileNameOfWallet="UTC--2018-06-29T04-33-11.713330800Z--ba28e714d5800ccc9701284372fc335b9bf4f643.json";
		//we will take the file name from the database that storeds the login credential along with wallet address file as well as deployed contracts
		File file1=new File(path+"/keystore/"+fileNameOfWallet);
		//File file1 = new File("C:\\Users\\user\\ethereum\\keystore"+"\\UTC--2018-06-29T04-33-11.713330800Z--ba28e714d5800ccc9701284372fc335b9bf4f643.json");
		System.out.println(file1);
		Credentials credentials = WalletUtils.loadCredentials(password, file1);
		System.out.println(credentials.getAddress());
		

		//to obtain address balance **currently 1.5 ether so can use alot
		//String address = credentials.getAddress();
		//EthGetBalance balance = web3.ethGetBalance(address, DefaultBlockParameterName.LATEST)
		//                              .sendAsync().get();
		//System.out.println(balance.getBalance());
		
		//case of new chat created *only uncomment if creating new contract
		//RemoteCall<ChatRecord> newChatHist=ChatRecord.deploy(web3, credentials, Contract.GAS_PRICE, Contract.GAS_LIMIT);
		//uncomment next line if u want the contract address of the new contract
		//String contractAddress=newChatHist.send().getContractAddress();
	//PLEASE DO NOT UNCOMMENT LINE 63 TO 71!!!!!
//		ChatRecord t=newChatHist.send();
//		newChatHist.send().saveChatData("hi im late").send();//the last send is to send the string to the contract
//		newChatHist.send().setToWho("cher").send();//same for this send(); //for some reason the deployed contract when variables are et, the variables are not saved
//		System.out.println(t.isValid()); //so i plan to create the contract in case of a new chat then obtain the contract address to load and edit the variables hence savin it
//		System.out.println(owner);
//		System.out.println(t.getToWho().toString());//only access the object
//		System.out.println(t.getToWho().send().toString());//need to use send to access into the object to obtain the string/value
//		System.out.println(t.getTotalMessages().send().intValue());
//		System.out.println(t.getChatData(BigInteger.ONE).send().toString());
		
		//immediately load new contract(change first parameter to contractAddress)  OR load old contract (take from database)
		ChatRecord chatHist = ChatRecord.load("0xc03dc4484b7016b283201af786eca7162c76c03e", web3, credentials, Contract.GAS_PRICE, Contract.GAS_LIMIT);
		//need to know who i sent these messages to which will become stored
		//chatHist.setToWho("jak").send();
		System.out.println(chatHist.getToWho().send().toString());
		//to test adding of new data, uncomment the next line
		chatHist.saveChatData("time").send();
		System.out.println("Success");
		int num=chatHist.getTotalMessages().send().intValue();
		System.out.println(num);
		for(int i=1;i<=num;i++) {
			System.out.println(chatHist.getChatData(BigInteger.valueOf(i)).send().toString());
		}
		//line 85 not neccessary only for testing cause need store contractAddress into database 
		//System.out.println(contractAddress);
	}
}