# RubricaClient

Cliente Java Web Start para assinatura digital de códigos hash recebidos por API RESTful.
Baseado em Demoiselle Signer Desktop. Desenvolvido em Java 8.

## Preparação para execução segura

Para execução segura em Java Web Start, o pacote JAR com as dependências deve ser assinado 
digitalmente e a cadeia de autoridades certificadoras do assinante deve ser reconhecida pela 
instalação local do Java no cliente. Autoridades certificadoras ICP-Brasil, como o SERPRO, não vêm 
pré-configuradas nas instalações do Java e dos sistemas operacionais, então precisam ser 
manualmente instaladas em cada sistema cliente. Os certificados podem ser exportados do token 
assinante ou baixados do endereço:
https://certificados.serpro.gov.br/arserpro/pages/information/certificate_chain.jsf

A instalação da cadeia de certificados do assinante do JAR deve ser feita no Painel de Controle Java
(jcontrol):
1. aba Segurança;
2. Gerenciar Certificados...;
3. Tipo de Certificado: CA de Signatário;
4. Importar.

O mesmo procedimento será necessário para a cadeia de certificados do servidor da API RESTful, caso 
seja diferente do assinante do JAR. Alternativamente, o certificado do servidor pode ser importado 
na categoria Tipo de Certificado: Local Seguro, desconsiderando-se a cadeia de autoridades
certificadoras.

Não é recomendado, mas também é possível acrescentar o domínio do codebase do RubricaClient na 
Lista de Exceções de Sites. Esta opção pode ser conveniente durante o desenvolvimento.

## Compatibilidade com tokens

O Demoiselle Signer foi testado com tokens Safenet, Aladdin e Watchdata. Em geral, todos são 
suportados.

O Demoiselle Signer pode não suportar o token Watchdata Watchkey especificamente em sistemas Linux
de 64 bits com Java 8. Esse token foi testado com sucesso em sistemas Linux de 32 bits e em 
sistemas Windows com Java 7.

Sistemas Windows atualmente só suportam assinaturas com códigos hash de 256 bits; 512 bits não são
suportados. Há que se observar a compatibilidade com padrões futuros ICP-Brasil.

## Identificação de erros

O RubricaClient imprime no console todas as informações importantes de sua execução.
Para visualizá-las, habilitar a opção Mostrar Console na aba Avançado do Painel de Controle Java.