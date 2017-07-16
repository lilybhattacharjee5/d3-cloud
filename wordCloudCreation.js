var xmlhttp = new XMLHttpRequest();

var frequency_list = [];

xmlhttp.onreadystatechange = function() {

    // if page is loaded and ready
    if (this.readyState == 4 && this.status == 200) {

        $("#submit-button").on("click", function() {

            var numberOfWords = document.getElementById('number_of_words').value;
            document.getElementById('number_of_words').value = "";
            reloadChart(numberOfWords);

        });

    };

}

function reloadChart(numberOfWordsDisplayed) {
    // svg canvas variables
    var width = 1100;
    var height = 1500;

    // removes svg when bottom half of page is 'reloaded'
    d3.selectAll("svg").remove();
    frequency_list = [];

    // calls for data from ranks.txt
    myArray = JSON.parse(xmlhttp.responseText);

    console.log(myArray.length);
    if (numberOfWordsDisplayed > myArray.length) {
        $('#response1').html("Word Limit: " + myArray.length + " most common. Please enter a lower number.");
    } else {
        $('#response1').html("The number of words displayed in the word cloud is " + numberOfWordsDisplayed + ".");
    }

    $(myArray.slice(0, numberOfWordsDisplayed)).each(function(i) {
        frequency_list.push({"text" : myArray[i][0].text.toString(), "size" : parseInt(myArray[i][1].size.toString())});
    });

    var color = d3.scale.linear()
    .domain([0,1,2,3,4,5,6,10,15,20,100])
    .range(["#B6F3FC", "#A6E2F3", "#96D1EA", "#86C0E1", "#76B0D9", "#669FD0", "#568EC7", "#467DBE", "#194FA6", "#255CAD", "#164BA4", "#063B9C"]);

    d3.layout.cloud().size([width, height])
    .words(frequency_list)
    .rotate(0)
    .fontSize(function(d) { return d.size; })
    .on("end", draw)
    .start();

    function draw(words) {
        d3.select(".word-cloud").append("svg")
        .attr("width", width + 250)
        .attr("height", height)
        .attr("font-family", "Lato")
        .attr("font-weight", "bold")
        .append("g")

        // without the transform, words words would get cutoff to the left and top, they would appear outside of the SVG area
        .attr("transform", "translate(530, 730)")

        .selectAll("text")
        .data(words)
        .enter().append("text")
        .style("font-size", function(d) { return d.size + "px"; })
        .style("fill", function(d, i) { return color(i); })
        .attr("transform", function(d) {
            return "translate(" + [d.x, d.y] + ")rotate(" + d.rotate + ")";
        })
        .text(function(d) { return d.text; })
    }
}

xmlhttp.open("GET", "ranks.txt", true);
xmlhttp.send();