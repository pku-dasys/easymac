#include <bits/stdc++.h>
using namespace std;

const char* ADDR1="fh2.txt";
const char* ADDR2="v2.txt";
const int BITS=16;
const int COLUMNS=BITS+BITS-1;
const int MAX_DEPTH=6;
int f[MAX_DEPTH][COLUMNS];
int h[MAX_DEPTH][COLUMNS];
int initV[COLUMNS];
int V[COLUMNS];

void init()
{
	int i;
	for(i=0;i<BITS;i++)
    	initV[i]=i+1;
 	for(i=BITS;i<COLUMNS;i++)
    	initV[i]=COLUMNS-i;
}

void output()
{
	freopen(ADDR1,"w",stdout);
	int i,j;
	cout<<COLUMNS<<" "<<MAX_DEPTH<<endl;
	for(j=COLUMNS-1;j>=0;j--)
	{
		V[j]=initV[j];
		cout<<V[j]<<" ";
	}
	cout<<endl;
	for(i=0;i<MAX_DEPTH;i++)
	{
		for(j=COLUMNS-1;j>=0;j--)
		{
			V[j]-=2*f[i][j]+h[i][j];
			if(j)V[j]+=f[i][j-1]+h[i][j-1];
			cout<<f[i][j]<<" "<<h[i][j]<<"   ";
		}
		cout<<endl;
	}
	freopen(ADDR2,"w",stdout);
	cout<<COLUMNS<<endl;
	for(i=COLUMNS-1;i>=0;i--)
	    cout<<V[i]<<" ";
	exit(0);
}

void dfs(int nowlt,int t,int lt,int i,int V,int flg1,int flg2)
/*i--now at i-column, t--now depth, lt--(i-1)-column depth, V--now rest V nodes, flg1&2--flags for search*/
{
	if(i==COLUMNS)
	{
		output();
		return ;
	}
	int nowflg1=0;
	if(t&&i)
	{
	    V+=f[t-1][i-1]+h[t-1][i-1];
	    if(f[t-1][i-1]+h[t-1][i-1])nowflg1=1;
	}
	
	if(V<=2&&t>lt)
	{
		if(flg1||!flg2)
    	    dfs(-1,0,nowlt,i+1,initV[i+1],0,0);
	    if(V<=1)return ;
	    else {flg1=0;flg2=1;}
	}
	if(V>=3&&i==COLUMNS-1)return ;
	if(t==MAX_DEPTH)return ;
	
	f[t][i]=V/3;h[t][i]=0;
	dfs((V/3)?t:nowlt,t+1,lt,i,V-(2*f[t][i]+h[t][i]),(V/3)?1:flg1,flg2);
	f[t][i]=0;h[t][i]=0;
	
	if(V%3==2&&(V>2||nowflg1))
	{
	   f[t][i]=V/3;h[t][i]=1;
	   dfs(t,t+1,lt,i,V-(2*f[t][i]+h[t][i]),1,flg2);
	   f[t][i]=0;h[t][i]=0;
	}
}

int main()
{
	init();
	dfs(-1,0,0,0,initV[0],0,0);
	return 0;
}
