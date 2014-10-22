campbus
=======

O intuito deste projeto é criar um aplicativo móvel (para dispositivos Android), com funcionalidades de identificação das linhas de ônibus da cidade de Campinas, seus respectivos pontos de parada e criação de um mecanismo roteirizador entre outras funcionalidades inseridas no tema de mobilidade urbana relacionada ao transporte coletivo.

Esta iniciativa visa suprir a carência e deficiências das ferramentas fornecidas pelos órgãos públicos e concessionárias à população de Campinas, tornando a atividade de locomoção através do transporte público uma tarefa mais fácil e confortável.

Os dados que alimentam este sistema foram extraídos dos sistemas do Portal InterBus e da EMDEC:

   ->  http://www.portalinterbuss.com.br/campinas/
 
   ->  http://www.emdec.com.br/ABusInf/

Atualmente a base de dados é composta por todas as linhas de circulares que atendem a cidade de Campinas e está estruturada em arquivos JSON, contendo as seguintes informações:

	- Número da Linha
	- Nome da Linha
	- Nome da Empresa Concessionária
	- Itinerários (sentido de ida & volta) expressos na forma de nome de ruas sem numeração ou quaisquer referências de geolocalização (algo que seria bastante interessante para implementação de funcionalidades como cálculo de distâncias);
	- Letreiro do ônibus em ambos os sentidos
	- Horários (em dias úteis, aos sábados e domingos & feriados)
	- Quantidade de veículos rodando (em dias úteis, aos sábados e domingos & feriados)
	- Tempo de viagem ponta-a-ponta
	- Extensão do itinerário

É sabido que várias coordenadas de geolocalização dos pontos pelos quais cada linha passa pode ser retirado do código-fonte do serviço de ilustração do itinerário da linha presente no sistema da EMDEC, sob URL http://www.emdec.com.br/ABusInf/ABInfSvItiDLGoogleM.asp?CdPjOID=XXXXX&TpDiaID=0, onde XXXXX deve ser substituído pelo código interno da linha, informação que está mapeada no arquivo IDMapping.csv. Entretando, até o presente momento não foi entendido como mapear os pontos de paradas das linhas com a informações de geolocalização presentes nesta URL.
