

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 3.4.0.
 */
public class ChatRecord extends Contract {
    private static final String BINARY = "6080604052600060035534801561001557600080fd5b5060008054600160a060020a03191633179055610573806100376000396000f3006080604052600436106100825763ffffffff7c0100000000000000000000000000000000000000000000000000000000600035041663147e9108811461008757806341c0e1b5146100ae5780634a9b5f76146100c55780636cfb5de81461014f5780637c3a4e4f146101a8578063893d20e814610201578063d850de6f1461023f575b600080fd5b34801561009357600080fd5b5061009c610257565b60408051918252519081900360200190f35b3480156100ba57600080fd5b506100c361025e565b005b3480156100d157600080fd5b506100da61029b565b6040805160208082528351818301528351919283929083019185019080838360005b838110156101145781810151838201526020016100fc565b50505050905090810190601f1680156101415780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34801561015b57600080fd5b506040805160206004803580820135601f81018490048402850184019095528484526100c394369492936024939284019190819084018382808284375094975061032e9650505050505050565b3480156101b457600080fd5b506040805160206004803580820135601f81018490048402850184019095528484526100c394369492936024939284019190819084018382808284375094975061037f9650505050505050565b34801561020d57600080fd5b50610216610396565b6040805173ffffffffffffffffffffffffffffffffffffffff9092168252519081900360200190f35b34801561024b57600080fd5b506100da6004356103b2565b6003545b90565b60005473ffffffffffffffffffffffffffffffffffffffff163314156102995760005473ffffffffffffffffffffffffffffffffffffffff16ff5b565b60028054604080516020601f60001961010060018716150201909416859004938401819004810282018101909252828152606093909290918301828280156103245780601f106102f957610100808354040283529160200191610324565b820191906000526020600020905b81548152906001019060200180831161030757829003601f168201915b5050505050905090565b600180548082018083556000929092528251610371917fb10e2d527612073b26eecdfd717e6a320cf44b4afac2b0732d9fcbe2b7fa0cf6019060208501906104af565b505060038054600101905550565b80516103929060029060208401906104af565b5050565b60005473ffffffffffffffffffffffffffffffffffffffff1690565b6060600354600014806103c3575081155b15610402575060408051808201909152600a81527f4e6f20486973746f72790000000000000000000000000000000000000000000060208201526104aa565b60018054600019840190811061041457fe5b600091825260209182902001805460408051601f60026000196101006001871615020190941693909304928301859004850281018501909152818152928301828280156104a25780601f10610477576101008083540402835291602001916104a2565b820191906000526020600020905b81548152906001019060200180831161048557829003601f168201915b505050505090505b919050565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106104f057805160ff191683800117855561051d565b8280016001018555821561051d579182015b8281111561051d578251825591602001919060010190610502565b5061052992915061052d565b5090565b61025b91905b8082111561052957600081556001016105335600a165627a7a72305820936f6f26bacb8e4e2ed2263f261ad91f9a25abaf07a9d31f3cd55b41a8cf923c0029";

    public static final String FUNC_GETTOTALMESSAGES = "getTotalMessages";

    public static final String FUNC_KILL = "kill";

    public static final String FUNC_GETTOWHO = "getToWho";

    public static final String FUNC_SAVECHATDATA = "saveChatData";

    public static final String FUNC_SETTOWHO = "setToWho";

    public static final String FUNC_GETOWNER = "getOwner";

    public static final String FUNC_GETCHATDATA = "getChatData";

    protected ChatRecord(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected ChatRecord(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public RemoteCall<BigInteger> getTotalMessages() {
        final Function function = new Function(FUNC_GETTOTALMESSAGES, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> kill() {
        final Function function = new Function(
                FUNC_KILL, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> getToWho() {
        final Function function = new Function(FUNC_GETTOWHO, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> saveChatData(String _data) {
        final Function function = new Function(
                FUNC_SAVECHATDATA, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_data)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> setToWho(String _to) {
        final Function function = new Function(
                FUNC_SETTOWHO, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_to)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> getOwner() {
        final Function function = new Function(FUNC_GETOWNER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<String> getChatData(BigInteger _msgNo) {
        final Function function = new Function(FUNC_GETCHATDATA, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_msgNo)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public static RemoteCall<ChatRecord> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(ChatRecord.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<ChatRecord> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(ChatRecord.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public static ChatRecord load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new ChatRecord(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static ChatRecord load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new ChatRecord(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }
}
