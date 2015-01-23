package com.gi.giserver.core.service.mapservice.desc;

import java.io.File;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.gi.giserver.core.i18n.ResourceManager;

public class MapServiceDesc
{
  static Logger logger = Logger.getLogger(MapServiceDesc.class);

  private boolean autoStart = false;
  private int minInstances = 1;
  private int maxInstances = 10;
  private int getTimeout = 120;
  private boolean needToken = false;
  private int dpi = 96;
  private int maxResults = -1;
  private String outputDir = null;
  private boolean useTile = false;
  private TileDesc tileDesc = null;

  public boolean isAutoStart() {
    return this.autoStart;
  }

  public int getMinInstances() {
    return this.minInstances;
  }

  public int getMaxInstances() {
    return this.maxInstances;
  }  

  public int getGetTimeout() {
	return getTimeout;
}

public boolean isNeedToken() {
    return this.needToken;
  }

  public int getDpi() {
    return this.dpi;
  }

  public int getMaxResults() {
    return this.maxResults;
  }

  public String getOutputDir() {
    return this.outputDir;
  }

  public boolean isUseTile() {
    return this.useTile;
  }

  public TileDesc getTileDesc() {
    return this.tileDesc;
  }

  public MapServiceDesc()
  {
  }

  public MapServiceDesc(String mapServiceDescFilePath)
  {
    loadFromFile(mapServiceDescFilePath);
  }

  public boolean loadFromFile(String filePath) {
    boolean result = false;
    try
    {
      File file = new File(filePath);
      SAXReader saxReader = new SAXReader();
      Document document = saxReader.read(file);
      Element root = document.getRootElement();
      try
      {
        this.autoStart = Boolean.parseBoolean(root
          .elementTextTrim("AutoStart"));
      } catch (Exception ex) {
      }
      try {
        this.minInstances = Integer.parseInt(root
          .elementTextTrim("MinInstances"));
      } catch (Exception ex) {
      }
      try {
        this.maxInstances = Integer.parseInt(root
          .elementTextTrim("MaxInstances"));
      } catch (Exception ex) {
      }
      try {
          this.getTimeout = Integer.parseInt(root
            .elementTextTrim("GetTimeout"));
        } catch (Exception ex) {
        }
      try {
        this.needToken = Boolean.parseBoolean(root
          .elementTextTrim("NeedToken"));
      } catch (Exception ex) {
      }
      try {
        this.dpi = Integer.parseInt(root.elementTextTrim("DPI"));
      } catch (Exception ex) {
      }
      try {
        this.maxResults = Integer.parseInt(root
          .elementTextTrim("MaxResults"));
      } catch (Exception ex) {
      }
      try {
        this.outputDir = root.elementTextTrim("OutputDir");
      }
      catch (Exception ex) {
      }
      Element eTile = null;
      try {
        eTile = root.element("Tile");
        this.useTile = Boolean.parseBoolean(eTile
          .attributeValue("enable"));
      }
      catch (Exception ex) {
      }
      if ((this.useTile) && (eTile != null)) {
        this.tileDesc = new TileDesc();
        try {
          this.tileDesc.setTilesDir(eTile.elementTextTrim("TilesDir"));
        } catch (Exception ex) {
        }
        try {
          this.tileDesc.setCreateOnDemand(Boolean.parseBoolean(eTile
            .elementTextTrim("CreateOnDemand")));
        } catch (Exception ex) {
        }
        try {
            this.tileDesc.setCreateSpread(Integer.parseInt(eTile
              .elementTextTrim("CreateSpread")));
          } catch (Exception ex) {
          }
        try {
          this.tileDesc.setFormat(eTile.elementTextTrim("Format"));
        } catch (Exception ex) {
        }
        try {
          this.tileDesc.setWidth(Integer.parseInt(eTile
            .elementTextTrim("Width")));
        } catch (Exception ex) {
        }
        try {
          this.tileDesc.setHeight(Integer.parseInt(eTile
            .elementTextTrim("Height")));
        } catch (Exception ex) {
        }
        try {
          this.tileDesc.setOriginX(Double.parseDouble(eTile
            .elementTextTrim("OriginX")));
        } catch (Exception ex) {
        }
        try {
          this.tileDesc.setOriginY(Double.parseDouble(eTile
            .elementTextTrim("OriginY")));
        }
        catch (Exception ex) {
        }
        Element eTileLods = eTile.element("Lods");
        this.tileDesc.clearTileLodDescs();
        for (Iterator iTileLod = eTileLods.elementIterator(); iTileLod
          .hasNext(); )
        {
          Element eTileLod = (Element)iTileLod.next();
          TileLodDesc tileLodDesc = new TileLodDesc();
          tileLodDesc.setLevel(Integer.parseInt(eTileLod
            .attributeValue("level")));
          tileLodDesc.setResolution(Double.parseDouble(eTileLod
            .attributeValue("resolution")));
          tileLodDesc.setScale(Double.parseDouble(eTileLod
            .attributeValue("scale")));
          this.tileDesc.addTileLodDesc(tileLodDesc);
        }
      }

      result = true;
    } catch (Exception ex) {
      logger.error(ResourceManager.getResourceBundleMapServiceLog().getString(
        "ERROR.LOAD_MAP_SERVICE_DESC_FROM_FILE"), ex);
    }

    return result;
  }
}