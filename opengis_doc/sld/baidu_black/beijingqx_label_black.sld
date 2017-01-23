<?xml version="1.0" encoding="GBK"?>  
<sld:StyledLayerDescriptor xmlns="http://www.opengis.net/sld" xmlns:sld="http://www.opengis.net/sld" xmlns:ogc="http://www.opengis.net/ogc" xmlns:gml="http://www.opengis.net/gml" version="1.0.0">  
    <sld:UserLayer> 
      <sld:LayerFeatureConstraints>  
            <sld:FeatureTypeConstraint/>  
        </sld:LayerFeatureConstraints> 
      <sld:UserStyle>
        <sld:Name>beijinglabeltest</sld:Name>  
        <sld:FeatureTypeStyle>
            <sld:Rule>
          <sld:Name>Single symbol</sld:Name>
          <sld:TextSymbolizer>
            <sld:Label>
              <ogc:PropertyName>name</ogc:PropertyName>
            </sld:Label>
            <sld:Font>
            <sld:CssParameter name="font-family">微软雅黑</sld:CssParameter>
            <sld:CssParameter name="font-size">12</sld:CssParameter>
            <sld:CssParameter name="font-style">normal</sld:CssParameter>
            <sld:CssParameter name="font-weight">bold</sld:CssParameter>
            </sld:Font>
            <sld:Fill>
              <sld:CssParameter name="fill">#FFFFFF</sld:CssParameter>
            </sld:Fill>
          </sld:TextSymbolizer>
        </sld:Rule>
        </sld:FeatureTypeStyle>
      </sld:UserStyle>  
    </sld:UserLayer>  
</sld:StyledLayerDescriptor>