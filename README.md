hdao java sdk
==================

this is hdao project's java sdk. You can use this sdk to integrated with hdao contracts.

# Usage

* `git clone https://github.com/BlockLink/hxjava.git` and cd hxjava
* `mvn package install -Dmaven.test.skip=true` to install hxjava lib locally
* clone this repo to another place by `git clone https://github.com/hyperdao/hdao_java_sdk.git` and cd hdao_java_sdk
* ``mvn package install -Dmaven.test.skip=true` to install hdaojava sdk lib locally`
* add hdao:hdaojava:${version} dependency to pom.xml or build.gradle

# Apis

* sign transaction

```
HdaoTransactionBuilder.signTransaction(Transaction tx, String wif, String chainId, String addressPrefix)
```

* open cdc
```
HdaoTransactionBuilder.openCdc(NodeClient nodeClient, String callerAddr, String callerPubKey, String cdcContractAddress,
                                                                BigDecimal collateralAmount, String collateralAssetId,
                                                            int collateralAssetPrecision, BigDecimal stableCoinAmount, int stableCoinPrecision)
```

* add collateral asset to cdc

```
HdaoTransactionBuilder.addCollateral(NodeClient nodeClient, String callerAddr, String callerPubKey, String cdcContractAddress,
                                       String cdcId, BigDecimal collateralAmount, String collateralAssetId,
                                       int collateralAssetPrecision)
```

* generate stable coin
```
HdaoTransactionBuilder.generateStableCoin(NodeClient nodeClient, String callerAddr, String callerPubKey, String cdcContractAddress,
                                                                   String cdcId, BigDecimal stableCoinAmount, int stableCoinPrecision)
```

* transfer cdc token
```
HdaoTransactionBuilder.transferCdc(NodeClient nodeClient, String callerAddr, String callerPubKey, String cdcContractAddress,
                                           String cdcId, String toAddr)
```

# Demo

* get transaction ref info
```
private String getRefInfo() throws NodeException {
    String nodeRpcEndpoint = "ws://nodeapi2.hxlab.org:6090";
    NodeClient nodeClient = new NodeClient(nodeRpcEndpoint);
    nodeClient.open();
    nodeClient.sendLogin();
    return nodeClient.getRefInfo();
}
```

* create and sign transfer transaction

```
String refInfo = getRefInfo();
String chainId = Constants.mainnetChainId;
String wifStr = "";
String fromAddr = "";
String toAddr = "";
BigDecimal amount = new BigDecimal("0.001");
BigDecimal fee = new BigDecimal("0.0011");
String memo = "test";
Transaction tx = TransactionBuilder.createTransferTransaction(refInfo, fromAddr, toAddr, amount, "1.3.0", 5, fee, memo, null);
String txJson = TransactionBuilder.signTransaction(tx, wifStr, chainId, Address.ADDRESS_PREFIX);
log.info("signed tx: {}", txJson);
// then you can use client rpc's lightwallet_broadcast or node rpc to broadcast this signed transaction json
```

* invoke contract transaction

```
String refInfo = getRefInfo();
String chainId = Constants.mainnetChainId;
String wifStr = "";
String callerAddr = "";
String callerPubKey = "";
String contractId = "HXCHcRE3jsyHGrtoE2ZJZtpHsEiYTQ7VrHkb";
String contractApi = "balanceOf";
String contractArg = "";

long gasLimit = 10000;
long gasPrice = 1;

BigDecimal fee = new BigDecimal("0.003");

Transaction tx = TransactionBuilder.createContractInvokeTransaction(refInfo, callerAddr, callerPubKey,
        contractId, contractApi, contractArg, fee, gasLimit, gasPrice, null);
String txJson = TransactionBuilder.signTransaction(tx, wifStr, chainId, Address.ADDRESS_PREFIX);
log.info("signed tx: {}", txJson);
```

* transfer asset to contract transaction
```
String refInfo = getRefInfo();
String chainId = Constants.mainnetChainId;
String wifStr = "";
String callerAddr = "";
String callerPubKey = "";
String contractId = "HXCHcRE3jsyHGrtoE2ZJZtpHsEiYTQ7VrHkb";
BigDecimal transferAmount = new BigDecimal("0.001");
String transferMemo = "hi";

long gasLimit = 10000;
long gasPrice = 1;

BigDecimal fee = new BigDecimal("0.003");

Transaction tx = TransactionBuilder.createContractTransferTransaction(refInfo, callerAddr, callerPubKey,
        contractId, transferAmount, "1.3.0", Constants.hxPrecision, transferMemo, fee, gasLimit, gasPrice, null);
String txJson = TransactionBuilder.signTransaction(tx, wifStr, chainId, Address.ADDRESS_PREFIX);
log.info("signed tx: {}", txJson);
```

* generate private key and address

```
ECKey ecKey = PrivateKeyGenerator.generate();
String privateKeyHex = ecKey.getPrivateKeyAsHex();
String wif = PrivateKeyUtil.privateKeyToWif(ecKey);
log.info("privateKeyHex: {}", privateKeyHex);
log.info("privateKey wif: {}", wif);
Address address = Address.fromPubKey(ecKey.getPubKey(), AddressVersion.NORMAL);
log.info("address: {}", address);
```

* get data from network node rpc

```
String nodeRpcEndpoint = "ws://nodeapi2.hxlab.org:6090";
NodeClient nodeClient = new NodeClient(nodeRpcEndpoint);
try {
    nodeClient.open();
    nodeClient.sendLogin();
    String addr = "";
    String pubKeyStr = "";
    String contractId = "HXCJb6BiDdSL9Duo9UtXaxncyRHcNsfQB7cV";
    String contractApi = "tokenName";
    String contractArg = "";
    String contractTxId = "";
    List<AmountInfoResponse> balances = nodeClient.getAddrBalances(addr);
    List<AssetInfoResponse> assets = nodeClient.listAssets();
    JSONObject testingResult = nodeClient.invokeContractTesting(pubKeyStr, contractId, contractApi, contractArg);
    String invokeResult = nodeClient.invokeContractOffline(pubKeyStr, contractId, contractApi, contractArg);
    log.info("balances: {}", JSON.toJSONString(balances));
    log.info("assets: {}", JSON.toJSONString(assets));
    log.info("testing result: {}", testingResult.toJSONString());
    log.info("invoke result: {}", invokeResult);
    String refInfo = nodeClient.constructRefInfo(6648413, "0065725d686b75c3d6576ecc17d5161c260d6dcd");
    log.info("example refInfo: {}", refInfo);
    JSONObject blockInfo = nodeClient.getBlock(100);
    log.info("blockInfo: {}", JSON.toJSONString(blockInfo));
    List<TxOperationReceiptResponse> txReceipts = nodeClient.getContractTxReceipts(contractTxId);
    log.info("txReceipts: {}", JSON.toJSONString(txReceipts));
} finally {
    nodeClient.close();
}
```