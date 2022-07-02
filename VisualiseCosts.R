library(ggplot2)


colours <- colorRampPalette(c("blue", "green", "yellow", "red"))(15)


for(i in 0:239){
  costs <- read.csv(paste("./CSV/costs", i, ".csv", sep = ""), sep = ";")
  colnames(costs) <- c("G", "N", "Value")
  map <- ggplot2::ggplot(costs,
                  mapping=ggplot2::aes(x=G,
                                       y=N,
                                       fill=Value)) +
    ggplot2::geom_tile() +
    ggplot2::scale_fill_stepsn(colours = colours,
                               breaks = c(-4, -2, 0, 2, 4, 7, 9, 11, 13, 18, 20, 22, 29, 31, 40),
                               labels = c("NNNN", "GNNN", "GGNN", "GGGN", "GGGG", "NNNX", "GNNX", "GGNX", "GGGX", "NNXX", "GNXX", "GGXX", "NXXX", "GXXX", "XXXX"),
                               limits = c(-4, 40),
                               guide = "legend")
  jpeg(paste("./Bilder/costs", i, ".jpg", sep = ""), width = 1000, height = 500)
  print(map)
  dev.off()
}