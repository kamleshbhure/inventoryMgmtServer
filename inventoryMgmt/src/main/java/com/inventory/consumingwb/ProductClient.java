package com.inventory.consumingwb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.core.SoapActionCallback;

import com.products.consumingwebservice.wsdl.ProductDetailsRequest;
import com.products.consumingwebservice.wsdl.ProductDetailsResponse;

public class ProductClient extends WebServiceGatewaySupport {

  private static final Logger log = LoggerFactory.getLogger(ProductClient.class);

  public ProductDetailsResponse getProduct(long productId) {

	ProductDetailsRequest request = new ProductDetailsRequest();
    request.setProductNumber(productId);

    ProductDetailsResponse response = (ProductDetailsResponse) getWebServiceTemplate()
        .marshalSendAndReceive("http://localhost:8090/soap-api/service/products?wsdl=ProductService.wsdl", request, null);

    return response;
  }

}