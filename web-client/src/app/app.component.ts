import { Component, OnInit } from '@angular/core';
import * as d3 from 'd3';
import { MainService } from "./main.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  title = 'web-Client';
  private data = [
    {"Framework": "Vue", "Stars": "166443"},
    {"Framework": "React", "Stars": "150793"},
    {"Framework": "Angular", "Stars": "62342"},
    {"Framework": "Backbone", "Stars": "27647"},
    {"Framework": "Ember", "Stars": "21471"},
  ];
  private svg: any;
  private margin = 50;
  private width = 750 - (this.margin * 2);
  private height = 400 - (this.margin * 2);

  constructor(private mainService: MainService) { }

  ngOnInit() {
    this.createSvg();
    this.drawBars(this.data);

    setInterval(() => {
      this.mainService.getLogLevelsData().subscribe((data: any) => {
        this.data = data.logLevelMetrics;
        this.svg.selectAll("*").remove();

        this.drawBars(this.data);
      });
    }, 1000);
  }

  private createSvg(): void {
    this.svg = d3.select("figure#bar")
      .append("svg")
      .attr("width", this.width + (this.margin * 2))
      .attr("height", this.height + (this.margin * 2))
      .append("g")
      .attr("transform", "translate(" + this.margin + "," + this.margin + ")");
  }
  private drawBars(data: any[]): void {
    // Create the X-axis band scale
    const x = d3.scaleBand()
      .range([0, this.width])
      .domain(data.map(d => d.logLevel))
      .padding(0.2);

    // Draw the X-axis on the DOM
    this.svg.append("g")
      .attr("transform", "translate(0," + this.height + ")")
      .call(d3.axisBottom(x))
      .selectAll("text")
      .attr("transform", "translate(-10,0)rotate(-45)")
      .style("text-anchor", "end");

    // Create the Y-axis band scale
    const y = d3.scaleLinear()
      .domain([0, 400])
      .range([this.height, 0]);

    // Draw the Y-axis on the DOM
    this.svg.append("g")
      .call(d3.axisLeft(y));

    // Create and fill the bars
    this.svg.selectAll("bars")
      .data(data)
      .enter()
      .append("rect")
      .attr("x", (d: any) => x(d.logLevel))
      .attr("y", (d: any) => y(d.logLevelCount))
      .attr("width", x.bandwidth())
      .attr("height", (d: any) => this.height - y(d.logLevelCount))
      .attr("fill", "#d04a35");
  }

}
