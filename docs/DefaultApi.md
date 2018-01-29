# DefaultApi

All URIs are relative to *http://api.binance.com/api/v3*

Method | HTTP request | Description
------------- | ------------- | -------------
[**price**](DefaultApi.md#price) | **GET** /ticker/price | 


<a name="price"></a>
# **price**
> List&lt;Coin&gt; price()





### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.DefaultApi;


DefaultApi apiInstance = new DefaultApi();
try {
    List<Coin> result = apiInstance.price();
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#price");
    e.printStackTrace();
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**List&lt;Coin&gt;**](Coin.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json, application/xml
 - **Accept**: application/xml, application/json

