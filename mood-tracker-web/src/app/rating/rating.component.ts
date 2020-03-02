import { Component, OnInit, Input } from '@angular/core';
import { NgForm } from '@angular/forms';
import { ChartType, ChartOptions } from 'chart.js';
import { SingleDataSet, Label, monkeyPatchChartJsLegend, monkeyPatchChartJsTooltip } from 'ng2-charts';
import { Mood } from '../mood';
import { ApiService } from '../api.service';

@Component({
  selector: 'app-rating',
  templateUrl: './rating.component.html',
  styleUrls: ['./rating.component.css']
})
export class RatingComponent implements OnInit {

  mood: Mood;
  messages: Array<string>;
  isSubmitted = false;
  isPopulated = false;
  public pieChartOptions: ChartOptions = {
    responsive: true,
  };
  public pieChartLabels: Label[] = ['Stressed', 'Grumpy', 'Meh', 'Normal', 'Happy'];
  public pieChartData: SingleDataSet = [];
  public pieChartType: ChartType = 'pie';
  public pieChartLegend = true;
  public pieChartPlugins = [];
  public pieChartColors: Array<any> = [{backgroundColor: ['#e84351', '#434a54', '#3ebf9b', '#4d86dc', '#f3af37']}];
  config: any;
  collection = { count: 0, data: [] };

  constructor(private apiService: ApiService) {
    monkeyPatchChartJsTooltip();
    monkeyPatchChartJsLegend();
  }

  ngOnInit() {
  }

  submitForm(form: NgForm) {
    this.isSubmitted = true;
    if (!form.valid) {
      return false;
    } else {
      this.mood = new Mood(form.value.rating, form.value.message);
      this.apiService.createMood(this.mood)
      .subscribe(indicator => {
        if (indicator) {
          let arr: any[];
          arr = [];
          Object.keys(indicator.ratings).forEach(key => {
            arr.push(indicator.ratings[key]);
            this.isPopulated = true;
          });
          this.pieChartData = arr;

          this.messages = indicator.messages;
          this.config = {
            itemsPerPage: 5,
            currentPage: 1,
            totalItems: this.messages.length
          };
        }
      });
    }
  }

  pageChanged(event) {
    this.config.currentPage = event;
  }
}
