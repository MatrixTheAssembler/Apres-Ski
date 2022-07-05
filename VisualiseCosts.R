library(ggplot2)


colours <- colorRampPalette(c("blue", "green", "yellow", "red"))(15)


for(i in 0:241){
  print(i)
  
  data <- read.csv(paste("./CSV/data", i, ".csv", sep = ""), sep = ";", header = FALSE)
  colnames(data) <- c("G", "N", "Value", "Aktion", "Cost")
  aktion <- data[, 1:3]
  cost <- data[,c(1, 2, 5)]
  
  
  aktionMap <- ggplot2::ggplot(aktion,
                  mapping=ggplot2::aes(x=G,
                                       y=N,
                                       fill=Value)) +
    ggplot2::geom_tile() +
    ggplot2::scale_fill_stepsn(colours = colours,
                               breaks = c(-4, -2, 0, 2, 4, 7, 9, 11, 13, 18, 20, 22, 29, 31, 40),
                               labels = c("NNNN", "GNNN", "GGNN", "GGGN", "GGGG", "NNNX", "GNNX", "GGNX", "GGGX", "NNXX", "GNXX", "GGXX", "NXXX", "GXXX", "XXXX"),
                               limits = c(-4, 40),
                               guide = "legend")
  jpeg(paste("./Bilder/Aktion/aktion", i, ".jpg", sep = ""), width = 1000, height = 500)
  print(aktionMap)
  dev.off()
  
  
  costMap <- ggplot2::ggplot(aktion,
                         mapping=ggplot2::aes(x=G,
                                              y=N,
                                              fill=Value)) +
    ggplot2::geom_tile() + 
    ggplot2::scale_fill_gradient(name = NULL, labels = NULL)
  jpeg(paste("./Bilder/Cost/cost", i, ".jpg", sep = ""), width = 1000, height = 500)
  print(costMap)
  dev.off()
}
