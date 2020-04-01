package hdao.sdk;

import cash.hx.hxjava.builder.TransactionBuilder;
import cash.hx.hxjava.client.NodeClient;
import cash.hx.hxjava.exceptions.TransactionException;
import cash.hx.hxjava.operation.NodeException;
import cash.hx.hxjava.transaction.Transaction;

import java.math.BigDecimal;
import java.math.BigInteger;
/**
* building HDao transactions
*/
public class HdaoTransactionBuilder {

    private static String getRefInfo(NodeClient nodeClient) throws NodeException {
        return nodeClient.getRefInfo();
    }

    private static BigDecimal getDefaultFee() {
        return new BigDecimal("0.005");
    }

    private static long getDefaultGasLimit() {
        return 100000L;
    }

    private static long getDefaultGasPrice() {
        return 1L;
    }

    private static Transaction simpleInvoke(NodeClient nodeClient, String callerAddr, String callerPubKey, String contractAddress,
                                            String apiName, String apiArg) throws NodeException, TransactionException {
        String refInfo = getRefInfo(nodeClient);
        BigDecimal fee = getDefaultFee();
        long gasLimit = getDefaultGasLimit();
        long gasPrice = getDefaultGasPrice();
        return TransactionBuilder.createContractInvokeTransaction(refInfo, callerAddr, callerPubKey, contractAddress,
                apiName, apiArg, fee, gasLimit, gasPrice, null);
    }

    private static Transaction simpleContractTransfer(NodeClient nodeClient, String callerAddr, String callerPubKey, String contractAddress,
                                                      BigDecimal transferAmount, String transferAssetId, int assetPrecision, String transferMemo) throws NodeException, TransactionException {
        String refInfo = getRefInfo(nodeClient);
        BigDecimal fee = getDefaultFee();
        long gasLimit = getDefaultGasLimit();
        long gasPrice = getDefaultGasPrice();
        return TransactionBuilder.createContractTransferTransaction(refInfo, callerAddr, callerPubKey, contractAddress,
                transferAmount, transferAssetId, assetPrecision, transferMemo, fee, gasLimit, gasPrice, null);
    }

    public static Transaction openCdc(NodeClient nodeClient, String callerAddr, String callerPubKey, String cdcContractAddress,
                                          BigDecimal collateralAmount, String collateralAssetId,
                                      int collateralAssetPrecision, BigDecimal stableCoinAmount, int stableCoinPrecision)
            throws TransactionException, NodeException {
        if(collateralAmount == null || collateralAmount.compareTo(new BigDecimal("0.00000001"))<=0) {
            throw new TransactionException("collateralAmount must > 0");
        }
        if(stableCoinAmount == null || stableCoinAmount.compareTo(new BigDecimal("0.00000001"))<=0) {
            throw new TransactionException("stableCoinAmount must > 0");
        }
        String param = String.format("openCdc,%s", stableCoinAmount.multiply(BigDecimal.valueOf(10).pow(stableCoinPrecision)));
        return simpleContractTransfer(nodeClient, callerAddr, callerPubKey, cdcContractAddress,
                collateralAmount, collateralAssetId, collateralAssetPrecision, param);
    }

    public static Transaction addCollateral(NodeClient nodeClient, String callerAddr, String callerPubKey, String cdcContractAddress,
                                      String cdcId, BigDecimal collateralAmount, String collateralAssetId,
                                      int collateralAssetPrecision)
            throws TransactionException, NodeException {
        if(collateralAmount == null || collateralAmount.compareTo(new BigDecimal("0.00000001"))<=0) {
            throw new TransactionException("collateralAmount must > 0");
        }
        String param = String.format("addCollateral,%s", cdcId);
        return simpleContractTransfer(nodeClient, callerAddr, callerPubKey, cdcContractAddress,
                collateralAmount, collateralAssetId, collateralAssetPrecision, param);
    }

    public static Transaction generateStableCoin(NodeClient nodeClient, String callerAddr, String callerPubKey, String cdcContractAddress,
                                            String cdcId, BigDecimal stableCoinAmount, int stableCoinPrecision)
            throws TransactionException, NodeException {
        if(stableCoinAmount == null || stableCoinAmount.compareTo(new BigDecimal("0.00000001"))<=0) {
            throw new TransactionException("stableCoinAmount must > 0");
        }
        String apiName = "expandLoan";
        String apiArg = String.format("%s,%s", cdcId, stableCoinAmount.multiply(BigDecimal.valueOf(10).pow(stableCoinPrecision)));
        return simpleInvoke(nodeClient, callerAddr, callerPubKey, cdcContractAddress, apiName, apiArg);
    }

    public static Transaction transferCdc(NodeClient nodeClient, String callerAddr, String callerPubKey, String cdcContractAddress,
                                          String cdcId, String toAddr) throws TransactionException, NodeException {
        String apiName = "transferCdc";
        String apiArg = String.format("%s,%s", cdcId, toAddr);
        return simpleInvoke(nodeClient, callerAddr, callerPubKey, cdcContractAddress, apiName, apiArg);
    }

    public static String signTransaction(Transaction tx, String wif, String chainId, String addressPrefix) throws TransactionException {
        return TransactionBuilder.signTransaction(tx, wif, chainId, addressPrefix);
    }

}
