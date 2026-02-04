FROM mcr.microsoft.com/dotnet/aspnet:8.0 AS base
WORKDIR /app
EXPOSE 8080

FROM mcr.microsoft.com/dotnet/sdk:8.0 AS build
WORKDIR /src
COPY ["logger-service.csproj", "./"]
RUN dotnet restore "./logger-service.csproj"
COPY . .
WORKDIR "/src/."
RUN dotnet build "logger-service.csproj" -c Release -o /app/build

FROM build AS publish
RUN dotnet publish "logger-service.csproj" -c Release -o /app/publish /p:UseAppHost=false

FROM base AS final
WORKDIR /app
COPY --from=publish /app/publish .
RUN mkdir -p /app/logs
ENTRYPOINT ["dotnet", "logger-service.dll"]
