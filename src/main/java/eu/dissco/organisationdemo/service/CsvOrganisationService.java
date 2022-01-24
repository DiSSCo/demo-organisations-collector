package eu.dissco.organisationdemo.service;

import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import eu.dissco.organisationdemo.domain.CsvOrganisation;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class CsvOrganisationService {

  public List<CsvOrganisation> retrieveOrganisations(String filename){
    var rowProcessor = new BeanListProcessor<>(CsvOrganisation.class);
    var settings = new CsvParserSettings();
    settings.setProcessor(rowProcessor);
    var parser = new CsvParser(settings);
    parser.parse(getClass().getClassLoader().getResourceAsStream(filename));
    return rowProcessor.getBeans().stream().filter(e -> e.getOrganisationName() != null).toList();
  }

}
