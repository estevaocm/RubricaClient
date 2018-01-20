# RubricaClient

Cliente JNLP para assinar hashes recebidos por API RESTful, baseado nos projetos Demoiselle Signer Desktop.


## Notas ao desenvolvedor

As informações abaixo se referem a uma versão anterior do cliente e devem ser 
atualizadas.

Os certificados reconhecidos são definidos pela dependência "demoiselle-
certificate-ca-icpbrasil", que aceita apenas certificados de produção. Para 
certificados de homologação, por exemplo, certificados A1 em arquivo usados 
para simular PJs, é necessário EXCLUIR o "demoiselle-certificate-ca-icpbrasil" e
INCLUIR o "demoiselle-certificate-ca-icpbrasil-homologacao".

Em ambientes de desenvolvimento, testes e homologação, tipicamente o servidor
apresenta-se para HTTPS com um certificado auto-assinado, diferente de produção,
onde apresenta-se com certificado com a devida cadeia válida ICP-Brasil. Isso
causa problemas no handshake da conexão no Java. Por isso, a classe
AssinadorClienteRest faz uma chamada estática ao método suspenderValidacaoCadeia
de ServerCertificateUtils. As versões de desenvolvimento, testes e homologação
devem fazer essa chamada para poder se conectar ao servidor com certificado
auto-assinado. Recomenda-se que essa chamada não seja executada pela versão de
produção, embora não haja previsão de falhas ou adulteração das operações mesmo
em ataques man-in-the-middle, já que o servidor verifica a validade de todos os
dados recebidos (descriptografa o texto assinado, verifica o certificado do
signatário, confere o CPF do signatário com o CPF da sessão etc.).

Tipicamente, geramos três versões: desenvolvimento (certificados de produção,
chamando suspenderValidacaoCadeia), homologação (certificados de homologação,
chamando suspenderValidacaoCadeia) e produção (certificados de produção, sem
chamar suspenderValidacaoCadeia). As versões são publicadas no endereço 
"/public/certificado/webstart".

Por padrão, a build maven produz pacotes auto-assinados. Para produção, é
necessário remover as configurações de auto-assinatura e assinar com certificado
oficial do Serpro.

Estêvão Monteiro, 7 de novembro de 2016