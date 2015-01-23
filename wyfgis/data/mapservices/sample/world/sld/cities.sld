<?xml version="1.0" encoding="UTF-8"?>
<sld:StyledLayerDescriptor xmlns:sld="http://www.opengis.net/sld" xmlns:ogc="http://www.opengis.net/ogc" xmlns:gml="http://www.opengis.net/gml" version="1.0.0">
    <sld:UserLayer>
        <sld:LayerFeatureConstraints>
            <sld:FeatureTypeConstraint/>
        </sld:LayerFeatureConstraints>
        <sld:UserStyle>
            <sld:Name>Default Styler</sld:Name>
            <sld:Title>Default Styler</sld:Title>
            <sld:Abstract/>
            <sld:FeatureTypeStyle>
                <sld:Name>name</sld:Name>
                <sld:Title>title</sld:Title>
                <sld:Abstract>abstract</sld:Abstract>
                <sld:FeatureTypeName>Feature</sld:FeatureTypeName>
                <sld:SemanticTypeIdentifier>generic:geometry</sld:SemanticTypeIdentifier>
                <sld:SemanticTypeIdentifier>colorbrewer:unique:custom</sld:SemanticTypeIdentifier>
                <sld:Rule>
                    <sld:Name>rule01</sld:Name>
                    <sld:Title>1,000,000 to 5,000,000</sld:Title>
                    <sld:Abstract>Abstract</sld:Abstract>
                    <ogc:Filter>
                        <ogc:PropertyIsEqualTo>
                            <ogc:PropertyName>POP_CLASS</ogc:PropertyName>
                            <ogc:Literal>1,000,000 to 5,000,000</ogc:Literal>
                        </ogc:PropertyIsEqualTo>
                    </ogc:Filter>
                    <sld:MaxScaleDenominator>1.7976931348623157E308</sld:MaxScaleDenominator>
                    <sld:PointSymbolizer>
                        <sld:Graphic>
                            <sld:Mark>
                                <sld:WellKnownName>circle</sld:WellKnownName>
                                <sld:Fill>
                                    <sld:CssParameter name="fill">
                                        <ogc:Literal>#FF8040</ogc:Literal>
                                    </sld:CssParameter>
                                    <sld:CssParameter name="fill-opacity">
                                        <ogc:Literal>1.0</ogc:Literal>
                                    </sld:CssParameter>
                                </sld:Fill>
                                <sld:Stroke>
                                    <sld:CssParameter name="stroke">
                                        <ogc:Literal>#000000</ogc:Literal>
                                    </sld:CssParameter>
                                    <sld:CssParameter name="stroke-linecap">
                                        <ogc:Literal>butt</ogc:Literal>
                                    </sld:CssParameter>
                                    <sld:CssParameter name="stroke-linejoin">
                                        <ogc:Literal>miter</ogc:Literal>
                                    </sld:CssParameter>
                                    <sld:CssParameter name="stroke-opacity">
                                        <ogc:Literal>0.7</ogc:Literal>
                                    </sld:CssParameter>
                                    <sld:CssParameter name="stroke-width">
                                        <ogc:Literal>1.0</ogc:Literal>
                                    </sld:CssParameter>
                                    <sld:CssParameter name="stroke-dashoffset">
                                        <ogc:Literal>0.0</ogc:Literal>
                                    </sld:CssParameter>
                                </sld:Stroke>
                            </sld:Mark>
                            <sld:Opacity>
                                <ogc:Literal>1.0</ogc:Literal>
                            </sld:Opacity>
                            <sld:Size>
                                <ogc:Literal>14</ogc:Literal>
                            </sld:Size>
                            <sld:Rotation>
                                <ogc:Literal>0.0</ogc:Literal>
                            </sld:Rotation>
                        </sld:Graphic>
                    </sld:PointSymbolizer>
                </sld:Rule>
                <sld:Rule>
                    <sld:Name>rule02</sld:Name>
                    <sld:Title>100,000 to 250,000</sld:Title>
                    <sld:Abstract>Abstract</sld:Abstract>
                    <ogc:Filter>
                        <ogc:PropertyIsEqualTo>
                            <ogc:PropertyName>POP_CLASS</ogc:PropertyName>
                            <ogc:Literal>100,000 to 250,000</ogc:Literal>
                        </ogc:PropertyIsEqualTo>
                    </ogc:Filter>
                    <sld:MaxScaleDenominator>1.7976931348623157E308</sld:MaxScaleDenominator>
                    <sld:PointSymbolizer>
                        <sld:Graphic>
                            <sld:Mark>
                                <sld:WellKnownName>circle</sld:WellKnownName>
                                <sld:Fill>
                                    <sld:CssParameter name="fill">
                                        <ogc:Literal>#0080FF</ogc:Literal>
                                    </sld:CssParameter>
                                    <sld:CssParameter name="fill-opacity">
                                        <ogc:Literal>1.0</ogc:Literal>
                                    </sld:CssParameter>
                                </sld:Fill>
                                <sld:Stroke>
                                    <sld:CssParameter name="stroke">
                                        <ogc:Literal>#000000</ogc:Literal>
                                    </sld:CssParameter>
                                    <sld:CssParameter name="stroke-linecap">
                                        <ogc:Literal>butt</ogc:Literal>
                                    </sld:CssParameter>
                                    <sld:CssParameter name="stroke-linejoin">
                                        <ogc:Literal>miter</ogc:Literal>
                                    </sld:CssParameter>
                                    <sld:CssParameter name="stroke-opacity">
                                        <ogc:Literal>0.7</ogc:Literal>
                                    </sld:CssParameter>
                                    <sld:CssParameter name="stroke-width">
                                        <ogc:Literal>1.0</ogc:Literal>
                                    </sld:CssParameter>
                                    <sld:CssParameter name="stroke-dashoffset">
                                        <ogc:Literal>0.0</ogc:Literal>
                                    </sld:CssParameter>
                                </sld:Stroke>
                            </sld:Mark>
                            <sld:Opacity>
                                <ogc:Literal>1.0</ogc:Literal>
                            </sld:Opacity>
                            <sld:Size>
                                <ogc:Literal>8</ogc:Literal>
                            </sld:Size>
                            <sld:Rotation>
                                <ogc:Literal>0.0</ogc:Literal>
                            </sld:Rotation>
                        </sld:Graphic>
                    </sld:PointSymbolizer>
                </sld:Rule>
                <sld:Rule>
                    <sld:Name>rule03</sld:Name>
                    <sld:Title>250,000 to 500,000</sld:Title>
                    <sld:Abstract>Abstract</sld:Abstract>
                    <ogc:Filter>
                        <ogc:PropertyIsEqualTo>
                            <ogc:PropertyName>POP_CLASS</ogc:PropertyName>
                            <ogc:Literal>250,000 to 500,000</ogc:Literal>
                        </ogc:PropertyIsEqualTo>
                    </ogc:Filter>
                    <sld:MaxScaleDenominator>1.7976931348623157E308</sld:MaxScaleDenominator>
                    <sld:PointSymbolizer>
                        <sld:Graphic>
                            <sld:Mark>
                                <sld:WellKnownName>circle</sld:WellKnownName>
                                <sld:Fill>
                                    <sld:CssParameter name="fill">
                                        <ogc:Literal>#008000</ogc:Literal>
                                    </sld:CssParameter>
                                    <sld:CssParameter name="fill-opacity">
                                        <ogc:Literal>1.0</ogc:Literal>
                                    </sld:CssParameter>
                                </sld:Fill>
                                <sld:Stroke>
                                    <sld:CssParameter name="stroke">
                                        <ogc:Literal>#000000</ogc:Literal>
                                    </sld:CssParameter>
                                    <sld:CssParameter name="stroke-linecap">
                                        <ogc:Literal>butt</ogc:Literal>
                                    </sld:CssParameter>
                                    <sld:CssParameter name="stroke-linejoin">
                                        <ogc:Literal>miter</ogc:Literal>
                                    </sld:CssParameter>
                                    <sld:CssParameter name="stroke-opacity">
                                        <ogc:Literal>0.7</ogc:Literal>
                                    </sld:CssParameter>
                                    <sld:CssParameter name="stroke-width">
                                        <ogc:Literal>1.0</ogc:Literal>
                                    </sld:CssParameter>
                                    <sld:CssParameter name="stroke-dashoffset">
                                        <ogc:Literal>0.0</ogc:Literal>
                                    </sld:CssParameter>
                                </sld:Stroke>
                            </sld:Mark>
                            <sld:Opacity>
                                <ogc:Literal>1.0</ogc:Literal>
                            </sld:Opacity>
                            <sld:Size>
                                <ogc:Literal>10</ogc:Literal>
                            </sld:Size>
                            <sld:Rotation>
                                <ogc:Literal>0.0</ogc:Literal>
                            </sld:Rotation>
                        </sld:Graphic>
                    </sld:PointSymbolizer>
                </sld:Rule>
                <sld:Rule>
                    <sld:Name>rule04</sld:Name>
                    <sld:Title>5,000,000 and greater</sld:Title>
                    <sld:Abstract>Abstract</sld:Abstract>
                    <ogc:Filter>
                        <ogc:PropertyIsEqualTo>
                            <ogc:PropertyName>POP_CLASS</ogc:PropertyName>
                            <ogc:Literal>5,000,000 and greater</ogc:Literal>
                        </ogc:PropertyIsEqualTo>
                    </ogc:Filter>
                    <sld:MaxScaleDenominator>1.7976931348623157E308</sld:MaxScaleDenominator>
                    <sld:PointSymbolizer>
                        <sld:Graphic>
                            <sld:Mark>
                                <sld:WellKnownName>circle</sld:WellKnownName>
                                <sld:Fill>
                                    <sld:CssParameter name="fill">
                                        <ogc:Literal>#FF0000</ogc:Literal>
                                    </sld:CssParameter>
                                    <sld:CssParameter name="fill-opacity">
                                        <ogc:Literal>1.0</ogc:Literal>
                                    </sld:CssParameter>
                                </sld:Fill>
                                <sld:Stroke>
                                    <sld:CssParameter name="stroke">
                                        <ogc:Literal>#000000</ogc:Literal>
                                    </sld:CssParameter>
                                    <sld:CssParameter name="stroke-linecap">
                                        <ogc:Literal>butt</ogc:Literal>
                                    </sld:CssParameter>
                                    <sld:CssParameter name="stroke-linejoin">
                                        <ogc:Literal>miter</ogc:Literal>
                                    </sld:CssParameter>
                                    <sld:CssParameter name="stroke-opacity">
                                        <ogc:Literal>0.7</ogc:Literal>
                                    </sld:CssParameter>
                                    <sld:CssParameter name="stroke-width">
                                        <ogc:Literal>1.0</ogc:Literal>
                                    </sld:CssParameter>
                                    <sld:CssParameter name="stroke-dashoffset">
                                        <ogc:Literal>0.0</ogc:Literal>
                                    </sld:CssParameter>
                                </sld:Stroke>
                            </sld:Mark>
                            <sld:Opacity>
                                <ogc:Literal>1.0</ogc:Literal>
                            </sld:Opacity>
                            <sld:Size>
                                <ogc:Literal>20</ogc:Literal>
                            </sld:Size>
                            <sld:Rotation>
                                <ogc:Literal>0.0</ogc:Literal>
                            </sld:Rotation>
                        </sld:Graphic>
                    </sld:PointSymbolizer>
                </sld:Rule>
                <sld:Rule>
                    <sld:Name>rule05</sld:Name>
                    <sld:Title>50,000 to 100,000</sld:Title>
                    <sld:Abstract>Abstract</sld:Abstract>
                    <ogc:Filter>
                        <ogc:PropertyIsEqualTo>
                            <ogc:PropertyName>POP_CLASS</ogc:PropertyName>
                            <ogc:Literal>50,000 to 100,000</ogc:Literal>
                        </ogc:PropertyIsEqualTo>
                    </ogc:Filter>
                    <sld:MaxScaleDenominator>1.7976931348623157E308</sld:MaxScaleDenominator>
                    <sld:PointSymbolizer>
                        <sld:Graphic>
                            <sld:Mark>
                                <sld:WellKnownName>circle</sld:WellKnownName>
                                <sld:Fill>
                                    <sld:CssParameter name="fill">
                                        <ogc:Literal>#8000FF</ogc:Literal>
                                    </sld:CssParameter>
                                    <sld:CssParameter name="fill-opacity">
                                        <ogc:Literal>1.0</ogc:Literal>
                                    </sld:CssParameter>
                                </sld:Fill>
                                <sld:Stroke>
                                    <sld:CssParameter name="stroke">
                                        <ogc:Literal>#000000</ogc:Literal>
                                    </sld:CssParameter>
                                    <sld:CssParameter name="stroke-linecap">
                                        <ogc:Literal>butt</ogc:Literal>
                                    </sld:CssParameter>
                                    <sld:CssParameter name="stroke-linejoin">
                                        <ogc:Literal>miter</ogc:Literal>
                                    </sld:CssParameter>
                                    <sld:CssParameter name="stroke-opacity">
                                        <ogc:Literal>0.7</ogc:Literal>
                                    </sld:CssParameter>
                                    <sld:CssParameter name="stroke-width">
                                        <ogc:Literal>1.0</ogc:Literal>
                                    </sld:CssParameter>
                                    <sld:CssParameter name="stroke-dashoffset">
                                        <ogc:Literal>0.0</ogc:Literal>
                                    </sld:CssParameter>
                                </sld:Stroke>
                            </sld:Mark>
                            <sld:Opacity>
                                <ogc:Literal>1.0</ogc:Literal>
                            </sld:Opacity>
                            <sld:Size>
                                <ogc:Literal>6</ogc:Literal>
                            </sld:Size>
                            <sld:Rotation>
                                <ogc:Literal>0.0</ogc:Literal>
                            </sld:Rotation>
                        </sld:Graphic>
                    </sld:PointSymbolizer>
                </sld:Rule>
                <sld:Rule>
                    <sld:Name>rule06</sld:Name>
                    <sld:Title>500,000 to 1,000,000</sld:Title>
                    <sld:Abstract>Abstract</sld:Abstract>
                    <ogc:Filter>
                        <ogc:PropertyIsEqualTo>
                            <ogc:PropertyName>POP_CLASS</ogc:PropertyName>
                            <ogc:Literal>500,000 to 1,000,000</ogc:Literal>
                        </ogc:PropertyIsEqualTo>
                    </ogc:Filter>
                    <sld:MaxScaleDenominator>1.7976931348623157E308</sld:MaxScaleDenominator>
                    <sld:PointSymbolizer>
                        <sld:Graphic>
                            <sld:Mark>
                                <sld:WellKnownName>circle</sld:WellKnownName>
                                <sld:Fill>
                                    <sld:CssParameter name="fill">
                                        <ogc:Literal>#FFFF00</ogc:Literal>
                                    </sld:CssParameter>
                                    <sld:CssParameter name="fill-opacity">
                                        <ogc:Literal>1.0</ogc:Literal>
                                    </sld:CssParameter>
                                </sld:Fill>
                                <sld:Stroke>
                                    <sld:CssParameter name="stroke">
                                        <ogc:Literal>#000000</ogc:Literal>
                                    </sld:CssParameter>
                                    <sld:CssParameter name="stroke-linecap">
                                        <ogc:Literal>butt</ogc:Literal>
                                    </sld:CssParameter>
                                    <sld:CssParameter name="stroke-linejoin">
                                        <ogc:Literal>miter</ogc:Literal>
                                    </sld:CssParameter>
                                    <sld:CssParameter name="stroke-opacity">
                                        <ogc:Literal>0.7</ogc:Literal>
                                    </sld:CssParameter>
                                    <sld:CssParameter name="stroke-width">
                                        <ogc:Literal>1.0</ogc:Literal>
                                    </sld:CssParameter>
                                    <sld:CssParameter name="stroke-dashoffset">
                                        <ogc:Literal>0.0</ogc:Literal>
                                    </sld:CssParameter>
                                </sld:Stroke>
                            </sld:Mark>
                            <sld:Opacity>
                                <ogc:Literal>1.0</ogc:Literal>
                            </sld:Opacity>
                            <sld:Size>
                                <ogc:Literal>12</ogc:Literal>
                            </sld:Size>
                            <sld:Rotation>
                                <ogc:Literal>0.0</ogc:Literal>
                            </sld:Rotation>
                        </sld:Graphic>
                    </sld:PointSymbolizer>
                </sld:Rule>
                <sld:Rule>
                    <sld:Name>rule07</sld:Name>
                    <sld:Title>Less than 50,000</sld:Title>
                    <sld:Abstract>Abstract</sld:Abstract>
                    <ogc:Filter>
                        <ogc:PropertyIsEqualTo>
                            <ogc:PropertyName>POP_CLASS</ogc:PropertyName>
                            <ogc:Literal>Less than 50,000</ogc:Literal>
                        </ogc:PropertyIsEqualTo>
                    </ogc:Filter>
                    <sld:MaxScaleDenominator>1.7976931348623157E308</sld:MaxScaleDenominator>
                    <sld:PointSymbolizer>
                        <sld:Graphic>
                            <sld:Mark>
                                <sld:WellKnownName>circle</sld:WellKnownName>
                                <sld:Fill>
                                    <sld:CssParameter name="fill">
                                        <ogc:Literal>#808080</ogc:Literal>
                                    </sld:CssParameter>
                                    <sld:CssParameter name="fill-opacity">
                                        <ogc:Literal>1.0</ogc:Literal>
                                    </sld:CssParameter>
                                </sld:Fill>
                                <sld:Stroke>
                                    <sld:CssParameter name="stroke">
                                        <ogc:Literal>#000000</ogc:Literal>
                                    </sld:CssParameter>
                                    <sld:CssParameter name="stroke-linecap">
                                        <ogc:Literal>butt</ogc:Literal>
                                    </sld:CssParameter>
                                    <sld:CssParameter name="stroke-linejoin">
                                        <ogc:Literal>miter</ogc:Literal>
                                    </sld:CssParameter>
                                    <sld:CssParameter name="stroke-opacity">
                                        <ogc:Literal>0.7</ogc:Literal>
                                    </sld:CssParameter>
                                    <sld:CssParameter name="stroke-width">
                                        <ogc:Literal>1.0</ogc:Literal>
                                    </sld:CssParameter>
                                    <sld:CssParameter name="stroke-dashoffset">
                                        <ogc:Literal>0.0</ogc:Literal>
                                    </sld:CssParameter>
                                </sld:Stroke>
                            </sld:Mark>
                            <sld:Opacity>
                                <ogc:Literal>1.0</ogc:Literal>
                            </sld:Opacity>
                            <sld:Size>
                                <ogc:Literal>4</ogc:Literal>
                            </sld:Size>
                            <sld:Rotation>
                                <ogc:Literal>0.0</ogc:Literal>
                            </sld:Rotation>
                        </sld:Graphic>
                    </sld:PointSymbolizer>
                </sld:Rule>
            </sld:FeatureTypeStyle>
            <sld:FeatureTypeStyle>
                <sld:Name>simple</sld:Name>
                <sld:Title>title</sld:Title>
                <sld:Abstract>abstract</sld:Abstract>
                <sld:FeatureTypeName>Feature</sld:FeatureTypeName>
                <sld:SemanticTypeIdentifier>generic:geometry</sld:SemanticTypeIdentifier>
                <sld:SemanticTypeIdentifier>simple</sld:SemanticTypeIdentifier>
                <sld:Rule>
                    <sld:Name>name</sld:Name>
                    <sld:Title>title</sld:Title>
                    <sld:Abstract>Abstract</sld:Abstract>
                    <sld:MaxScaleDenominator>3.0E7</sld:MaxScaleDenominator>
                    <sld:TextSymbolizer>
                        <sld:Label>
                            <ogc:PropertyName>CITY_NAME</ogc:PropertyName>
                        </sld:Label>
                        <sld:Font>
                            <sld:CssParameter name="font-family">
                                <ogc:Literal>Arial</ogc:Literal>
                            </sld:CssParameter>
                            <sld:CssParameter name="font-size">
                                <ogc:Literal>14.0</ogc:Literal>
                            </sld:CssParameter>
                            <sld:CssParameter name="font-style">
                                <ogc:Literal>normal</ogc:Literal>
                            </sld:CssParameter>
                            <sld:CssParameter name="font-weight">
                                <ogc:Literal>bold</ogc:Literal>
                            </sld:CssParameter>
                        </sld:Font>
                        <sld:LabelPlacement>
                            <sld:PointPlacement>
                                <sld:AnchorPoint>
                                    <sld:AnchorPointX>
                                        <ogc:Literal>0.5</ogc:Literal>
                                    </sld:AnchorPointX>
                                    <sld:AnchorPointY>
                                        <ogc:Literal>0.0</ogc:Literal>
                                    </sld:AnchorPointY>
                                </sld:AnchorPoint>
                                <sld:Displacement>
                                    <sld:DisplacementX>
                                        <ogc:Literal>0</ogc:Literal>
                                    </sld:DisplacementX>
                                    <sld:DisplacementY>
                                        <ogc:Literal>6</ogc:Literal>
                                    </sld:DisplacementY>
                                </sld:Displacement>
                                <sld:Rotation>
                                    <ogc:Literal>0.0</ogc:Literal>
                                </sld:Rotation>
                            </sld:PointPlacement>
                        </sld:LabelPlacement>
                        <sld:Fill>
                            <sld:CssParameter name="fill">
                                <ogc:Literal>#000000</ogc:Literal>
                            </sld:CssParameter>
                            <sld:CssParameter name="fill-opacity">
                                <ogc:Literal>1.0</ogc:Literal>
                            </sld:CssParameter>
                        </sld:Fill>
                        <sld:VendorOption name="spaceAround">1</sld:VendorOption>
                    </sld:TextSymbolizer>
                </sld:Rule>
            </sld:FeatureTypeStyle>
        </sld:UserStyle>
    </sld:UserLayer>
</sld:StyledLayerDescriptor>

